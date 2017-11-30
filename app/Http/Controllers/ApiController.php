<?php

namespace App\Http\Controllers;
use Tymon\JWTAuth\Facades\JWTAuth;
use Log;

class ApiController extends Controller
{
    // ApiRequestValidation variable
    // Handle request's response
    protected $_ARV;

    /** @var tools\JsonResponse Json response for api request
    *   By default success is loaded
    **/
    protected $_response;

    private $_currentUser;

    /**
     * Constructor
     **/
    public function __construct()
    {
        $this->_currentUser = null;
        /**
         * @var $_ARV handle api json response
         **/
        $this->_ARV = new \App\Http\Requests\ApiRequestValidation();

        $this->setDefaultSuccessJsonResponse();

        if (JWTAuth::getToken())
        {
            Log::debug("ApiController JWTAuth->getToken = " . JWTAuth::getToken());
            try
            {
                $this->_currentUser = JWTAuth::toUser(JWTAuth::getToken());
            }
            catch (\Tymon\JWTAuth\Exceptions\TokenExpiredException $expToken)
            {
                Log::debug("ApiController JWTAuth expired token");
            }
        }
    }

    public function getCurrentUser()
    {
        return $this->_currentUser;
    }

    //public function getDefaultJsonResponse()
    //{
    //    return $this->_response;
    //}

    public function getJsonResponse()
    {
        return $this->_response;
    }

    public function setDefaultFailureJsonResponse($optsErrorsFields = true)
    {
        $this->_response = $this->_ARV->getFailureJson($optsErrorsFields);
    }

    public function setManualJsonResponse($response)
    {
        $this->_response = $response;
    }

    public function setDefaultSuccessJsonResponse()
    {
        $this->_response = $this->_ARV->getSuccessJson();
    }

    /**
    * Get default json response string
    */
    public function getRawJsonResponse()
    {
      return $this->_response->getJson();
    }
}
