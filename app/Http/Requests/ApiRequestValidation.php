<?php

namespace App\Http\Requests;

use Validator;
use Illuminate\Support\Facades\Log;
use \App\Http\tools\JsonResponseFactory as JsonResponseFactory;

class ApiRequestValidation
{
    private $validator;

    public function __construct()
    {
        Log::alert("JsonResponseFactory create");
        $validator = null;
    }

    /**
     * Validate the request with given rules
     * True is returned when the request is validate
     * otherwise false
     * 
     * @param Illuminate\Http\Request $request
     * @param array $rules
     * @return boolean
     **/
    public function validate(\Illuminate\Http\Request $request, array $rules)
    {
        $isValid = false;
        $this->validator = Validator::make($request->all(), $rules);
        if (!$this->validator->fails())
        {
            $isValid = true;
        }

        return $isValid;
    }

    /**
     * Get errors from last validation process
     * Format is :
     *     JSON :   {'error' : ERRORS}
     * Where ERRORS is formatted by laravel's validation framework
     * 
     * @return \Illuminate\Support\MessageBag
     **/
    public function getFailureJson($addSpecificsValidationErrors = true)
    {
        /**
         * Addtionnal parameter
         **/
        $optsFields = [];
        if ($addSpecificsValidationErrors)
        {
            $optsFields['title'] = 'Fields validation error(s).';
        }
        $optsFields['status'] = 400;
        

        //Get JsonResponseFailure object
        //The object is built with validator's errors
        $jsonFailureResponse = $this->getJsonResponseFromFactory('failure');

        if ($this->validator != null)
        {
            $jsonFailureResponse->setErrors($this->validator->getMessageBag()->messages());
        }
        
        $jsonFailureResponse->setOptionnalFields($optsFields);

        return $jsonFailureResponse;
    }

    /**
     * Get the correct JsonObject from the given status
     * 
     * @see JsonResponseFactory
     * @return JsonResponse
     * 
     **/
    private function getJsonResponseFromFactory($status = 'success')
    {
        return \App\Http\tools\JsonResponseFactory::getJsonResponseByStatus($status);
    }

    /**
     * Create and return a JsonResponseSuccess
     * 
     * @return JsonResponseSuccess
     **/
    public function getSuccessJson()
    {
        $jsonSuccessResponse = $this->getJsonResponseFromFactory();

        return $jsonSuccessResponse;
    }
}
