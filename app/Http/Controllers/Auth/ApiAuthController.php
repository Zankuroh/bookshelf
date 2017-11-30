<?php
namespace App\Http\Controllers\Auth;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Tymon\JWTAuth\Exceptions\JWTExceptions;
use JWTAuth;
use Illuminate\Foundation\Auth\AuthenticateUsers;
use Socialite;
use Log;
use \App\Models\User as User;


class ApiAuthController extends \App\Http\Controllers\ApiController
{
  private $request;

  public function __construct()
  {
    $this->middleware('guest', ['except' => 'getLogout']);
    parent::__construct();
  }

  /**
  * Handle the profile loaded from the socialte driver and
  * create a JSON response accordingly and return it.
  * If the profile does not exist, we fillfull the response
  * with errors message otherwise the a new token of JWAuth is returned
  * into the response.
  *
  * @param $profile The fetched profile from driver (created inside the driver)
  * 
  * @return User or null
  */
  private function storeOrRetrieveUserFromSocialProfile($profile)
  {
    $success = true;

    if (!is_null($profile))
    {
      //$user = User::where('email', '=', $profile->getEmail())->first();

      $user = User::getByEmailOrCreate($profile->email);
      if (is_null($user))
      {
        $user = new User();
        $user->email = $profile->email;
        $user->name = $profile->name;
        $user->password = null; //we force to laravel db

        $user->setSocialNetworkFlag();
        $user->save();

        // NOT EXISTING WE CREATE USER
        Log::debug("NEW USER HAS BEEN CREATED FOR EMAIL : " . $user->email);
      }
      else
      {
        Log::debug("NEW USER HAS BEEN RETRIEVED : " . $user->email);
      }

      /**
      * Feed request manually to handle it in classical authenticate
      * function to authenticate the user as a normal person.
      */
      $this->request['email'] = $user->email;
      $this->request['password'] = null;

      Log::debug($profile->getEmail());
    }
    else
    {
      $success = false;
      $this->getJsonResponse()->setOptionnalFields(['title' => 'Connexion with ' . $providerName . ' is impossible - try again']);
    }

    return $success;
  }

  /**
  * This method will create an object with useful information about the
  * user (maybe new?) that trying to connect with his/her social account.
  * To use this method, the request fields should be validated otherwise
  * unknown errors will occurs
  * @return object or null with meta data obtained from social network --
  * in the case where false is returned we will build the json response
  * with errors message
  */
  public function loadDataFromSocialProvider($request)
  {
    $providerName = $request->provider;
    $token = $request->token;

    $driver = Socialite::driver($providerName);
    $profile = null;

    if (strtolower($providerName) == "google")
    {
      /**
      * Handle google's redirect uri mandatory parameter
      * otherwise the auth will not pass
      */
      if ($request->has("redirect_uri"))
      {
        $redirectUri = $request->input("redirect_uri");
    
        Log::debug("set new redirect uri " . $request->input('redirect_uri'));
        $driver->redirectUrl($request->input('redirect_uri'));

        $accessData = $driver->getAccessTokenResponse($token);
        $accessToken = $accessData['access_token'];

        $profile = $driver->userFromToken($accessToken);
      }
      else
      {
        Log::debug("Redirect uri parameter has not been suplied");
        $this->setDefaultFailureJsonResponse(false);
        $this->getJsonResponse()->setData(['errors' => 'redirect_uri_missing']);
        $this->getJsonResponse()->setOptionnalFields(['title' => 'Redirect uri missing is missing.']);
      }      
    }
    else if (strtolower($providerName) == "facebook")
    {
      Log::debug("Loading profile from Facebook");
      $profile = $driver->userFromToken($token);
      Log::debug("Profile : ");
      Log::debug("Email loaded is : " . $profile->email);
    }
    else
    {
      $this->setDefaultFailureJsonResponse(false);
      $this->getJsonResponse()->setData(['errors' => 'provider not exist']);
      $this->getJsonResponse()->setOptionnalFields(['title' => 'The provider : ' . $providerName . ' is not supported, please try to connect with another social network.']);
    }

    return $profile;
  }

  /**
  * Initial oauth function to handle social login
  *
  * @return JsonObject Json Response
  */
  public function oAuthenticate(Request $request)
  {
    $this->setRequest($request);

    Log::debug("SOCIALITE PHASE");
    if ($this->_ARV->validate($request,
    ['token' => 'required|string',
    'provider' => 'required|string']))
    {
      try
      {
        //load profile from fetched social provider data
        $profile = $this->loadDataFromSocialProvider($request);
        //check if the profile is completely filled and also created or already
        // a loaded from the existing Database
        if (!is_null($profile) && $this->storeOrRetrieveUserFromSocialProfile($profile))
        {
          $request->request->add(['email' => $profile->email]);
          Log::debug("OAuthSteps passed, next step of classical authentication begin");
          $this->authenticate($request, true);
        }
        else
        {
          Log::debug("Should not be in this step, failure during loading user from social driver");
        }
      }
      catch (Exception $e)
      {
        $this->setDefaultFailureJsonResponse(false);
        $this->getJsonResponse()->setOptionnalFields(['title' => 'Connexion with ' . $providerName . ' is impossible - try again']);
      }
      catch (\Guzzle\Http\Exception\BadResponseException $e)
      {
        $this->setDefaultFailureJsonResponse(false);
        $this->getJsonResponse()->setOptionnalFields(['title' => 'Connexion with ' . $providerName . ' is impossible - try again']);
        Log::debug('Uh oh! ' . $e->getMessage());
      }
      catch (\GuzzleHttp\Exception\ClientException $e)
      {
        $this->setDefaultFailureJsonResponse(false);
        $this->getJsonResponse()->setOptionnalFields(['title' => 'Connexion with ' . $request->provider . ' is impossible - try again']);
        Log::debug('Uh oh! ' . $e->getResponse()->getBody(true)->getContents());
        Log::debug('Uh oh! ' . $e->getResponse()->getStatusCode());
      }

    }
    else
    {
      $this->setDefaultFailureJsonResponse();
    }
    Log::debug("fuck1");

    return $this->getRawJsonResponse();
  }

  /**
  * Main function to handle authentication
  * Multiples fields belongs to the request are required
  * @see Validation fields
  */
  public function authenticate(Request $request, $socialAuth = false)
  {
    $authenticated = true;
    $failureAuthenticationReasons = null;
    $requiredFields = array('email' => 'required|email'); // default for all kind of loggin
    
    if (!$socialAuth)
    {
      array_push($requiredFields, ['password' => 'required|string']);
    }

    if ($this->_ARV->validate($request,$requiredFields))
    {
      $token = null;
      try
      {
        if ($socialAuth)
        {
          $user = User::getByEmail($request->email);
          if (!is_null($user))
          {
            $token = JWTAuth::fromUser($user);
          }
          else
          {
            $authenticated = false;
          }
          Log::debug("JWT AUTH SOCIAL AUTH : TOKEN : " . $token . " Request email : " . $request->email);
        }
        else
        {
          $credentials = $request->only('email', 'password');
          $token = JWTAuth::attempt($credentials);
        }
        if (is_null($token) || $token == false)
        {
          $authenticated = false;
          $failureAuthenticationReasons = ['title' => 'The email address or password you entered is not valid.'];
        }
      }
      catch (JWTException $ex)
      {
        $authenticated = false;
        $failureAuthenticationReasons = ['title' => 'The email address or password you entered is not valid.'];
      }

      if ($authenticated)
      {
        $user = JWTAuth::toUser($token);

        $this->getJsonResponse()->setData(['token' => $token, 'user_id' => $user->id]);
      }
      else
      {
        $this->setDefaultFailureJsonResponse();
      }
    }
    else
    {
      $this->setDefaultFailureJsonResponse();
    }
    $this->getJsonResponse()->setOptionnalFields($failureAuthenticationReasons);

    return $this->getRawJsonResponse();
  }

  public function setRequest(Request $request)
  {
    $this->request = $request;
  }

}
