<?php
namespace App\Http\Controllers\Auth;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Tymon\JWTAuth\Exceptions\JWTExceptions;
use JWTAuth;
use Illuminate\Foundation\Auth\AuthenticateUsers;

class ApiAuthController extends Controller
{
    public function __construct()
    {
        $this->middleware('guest', ['except' => 'getLogout']);    
    }

    public function authenticate(Request $request)
    {
        $credentials = $request->only('email', 'password');

        try
            {
                $token = JWTAuth::attempt($credentials);
                if (!$token)
                    {
                        return response()->json(['error' => 'User credentials are wrong'], 401);
                        
                    }
            }
        catch (JWTException $ex)
            {
                return response()->json(['error' => 'Something went wrong !'], 500);
            }

        return response()->json(compact('token'));
    }

}