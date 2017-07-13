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
    public function __construct()
    {
        $this->middleware('guest', ['except' => 'getLogout']);
        parent::__construct();
    }

    public function oauthenticate(Request $request)
    {
        $response = $this->getDefaultJsonResponse();

        Log::debug("SOCIALITE PHASE");
        if ($this->_ARV->validate($request, 
            ['token' => 'required|string',
            'provider' => 'required|string']))
        {
            $profile = null;
            try {

                $providerName = $request->input('provider');
                $token = $request->input('token');

                $driver = Socialite::driver($providerName);

                if ($request->input("redirect_uri"))
                {
                    $driver->redirectUrl($request->input('redirect_uri'));
                }

                if (strtolower($providerName) == "google")
                {
                    $accessData = $driver->getAccessTokenResponse($token);
                    $accessToken = $accessData['access_token'];

                    $profile = $driver->userFromToken($accessToken);
                }
                else if (strtolower($providerName) == "facebook")
                {
                    $profile = $driver->userFromToken($token);
                }
                else
                {
                    $response = $this->getDefaultFailureJsonResponse();
                    $response->setData(['errors' => 'provider not exist']);
                    $response->setOptionnalFields(['title' => 'The provider : ' . $providerName . ' is not supported, please try to connect with another social network.']);
                }

                if (!is_null($profile))
                {
                    $user = User::where('email', '=', $profile->getEmail())->first();

                    if (is_null($user))
                    {
                        $user = new User(['email' => $profile->getEmail(), 'name' => $profile->getName(), 'password' => null]);
                        $user->social_auth = true;
                        $user->save();
                    // NOT EXIST WE CREATE USER
                        Log::debug("NEW USER HAS BEEN CREATED FOR EMAIL : " . $user->email);

                    }
                    else
                    {
                        Log::debug("NEW USER HAS BEEN RETRIEVED : " . $user->email);
                    }

                    $request['email'] = $user->email;
                    $request['password'] = "RANDOM";

                    return $this->authenticate($request, true);
                    //ser::where('email', '=', $profile['email'])

                    Log::debug(print_r($profile));
                    Log::debug($profile->getEmail());
                }
                else
                {
                    $response->setOptionnalFields(['title' => 'Connexion with ' . $providerName . ' is impossible - try again']);
                }
            }
            catch (Exception $e)
            {
                $response = $this->getDefaultFailureJsonResponse();
                $response->setOptionnalFields(['title' => 'Connexion with ' . $providerName . ' is impossible - try again']);
            }
            catch (\Guzzle\Http\Exception\BadResponseException $e)
            {
                $response = $this->getDefaultFailureJsonResponse();
                $response->setOptionnalFields(['title' => 'Connexion with ' . $providerName . ' is impossible - try again']);
                Log::debug('Uh oh! ' . $e->getMessage());
            }
            catch (\GuzzleHttp\Exception\ClientException $e)
            {
                $response = $this->getDefaultFailureJsonResponse();
                $response->setOptionnalFields(['title' => 'Connexion with ' . $providerName . ' is impossible - try again']);
                Log::debug('Uh oh! ');
            }

        }
        Log::debug("fuck1");
            // else
            // {
            //     $response = $this->getDefaultFailureJsonResponse();
            // }




  //               $user = Socialite::driver('facebook')->userFromToken("EAADLl7HfK50BAPQyxZBSwTTi1vwZBlqtAYYvCZALsRSYUh2GpovEItidEN4kFQYv7I2cIOAPnCO7IqG59tcZCB0tbf7tkp9RedtfHXivjiB6bZBDaFVdTwSFKVqs7l9ifj1oEm1BferLWdpG6a1tO97mdFUoGFGJCdAeeqxTxz8pflQWSNjMs9oI8K17pSsH0xarZAzUfYANEpsycejAERFzWiwFZC1jogZD");


  //               //$user = Socialite::driver('google')->userFromToken("4/-SShABFa1LkkkE4gBQT2X7AEXlyqCdag1p0C8mKQUig");
  //               Log::debug(get_object_vars($user));

  //               $actual_link = "http://$_SERVER[HTTP_HOST]$_SERVER[REQUEST_URI]";
  //               Log::debug("LOCAL URI : " . $actual_link);

  //               $driver = Socialite::driver("google");
  //               $driverFB = Socialite::driver("facebook");

  //               $access_token = $driver->getAccessTokenResponse("4/7w792pCbais_kk5-OpFYGkHdnD_xaOOz0Dn1_eGxr8I");

  //               Log::debug("ACCESS DEBUG : ");
  //               Log::debug($access_token);

  // //              $access_token_fb = $driverFB->getAccessTokenResponse("4/9GMhPItm1zz8SjQ3u9v4bATOplxvYjvEoPhmgIGfccs");


  //               Log::debug("Foobar1");

  //              $socialUser = $driver->userFromToken($access_token['access_token']);


      //          $socialUserFB = $driverFB->userFromToken($access_token_fb);

            //     Log::debug(get_object_vars($socialUser));

            //     Log::debug("second user !!!!!!");
            //     Log::debug($socialUser);
            //     Log::debug("-----get access token");    
            // } catch (Exception $e) {
            //     $response->setData(['errors' => "bad instrutions"]);
            // } catch (\Guzzle\Http\Exception\ConnectException $ex)
            // {
            //     $response->setData(['errors' => 'bad intsutrction 2']);
            // } catch (\Guzzle\Http\Exception\BadResponseException $exc)
            // {
            //     $response->setData(['errors' => 'bad intsutrction 3']);
            // }
        // }


        return $response->getJson();
    }

    public function authenticate(Request $request, $socialAuth = false)
    {
        $response = $this->getDefaultJsonResponse();
        $authenticated = true;
        $failureAuthenticationReasons = null;

        if ($this->_ARV->validate($request, 
            ['email' => 'required|email',
            'password' => 'required|string']
            ))
        {
            $token = null;
            $credentials = $request->only('email', 'password');

            try
            {

                if ($socialAuth)
                {
                    $user = User::where('email', '=', $credentials['email'])->first();
                    if (!is_null($user))
                    {
                        $token = JWTAuth::fromUser($user);
                    }
                    else
                    {
                        $authenticated = false;
                    }
                    

                    Log::debug("JWT AUTH SOCIAL AUTH : TOKEN : " . $token);
                }
                else
                {
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
                $response->setData(['token' => $token, 'user_id' => $user->id]);
            }
            else
            {
                $response = $this->_ARV->getFailureJson();
            }
        }
        else
        {
            $response = $this->_ARV->getFailureJson();
        }
        $response->setOptionnalFields($failureAuthenticationReasons);

        return $response->getJson();
    }

}