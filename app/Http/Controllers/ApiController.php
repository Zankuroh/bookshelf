<?php 

namespace App\Http\Controllers;
use Tymon\JWTAuth\Facades\JWTAuth;

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

        $this->_response = $this->_ARV->getSuccessJson();

        if (JWTAuth::getToken())
        {
            $this->_currentUser = JWTAuth::toUser(JWTAuth::getToken());
        }
    }

    public function getCurrentUser()
    {
        return $this->_currentUser;
    }

    public function getDefaultJsonResponse()
    {
        return $this->_response;
    }

    public function getDefaultFailureJsonResponse($optsErrorsFields = true)
    {
        return $this->_ARV->getFailureJson($optsErrorsFields);
    }
}