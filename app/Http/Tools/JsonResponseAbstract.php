<?php

namespace App\Http\tools;

/**
 * Abstract class that handle JsonResponse
 * It able to fit with RFC 2119
 * 
 * @see https://tools.ietf.org/html/rfc2119
 * @see http://jsonapi.org/format/#content-negotiation-servers
 * 
 * Globally, the response will be at least composed of these fields:
 * 	- Error
 *  	- Data
 *   	- Meta
 *    	Others fields MAY be implemented thanks by optionnal fields function calls
 * 
 * @see http://jsonapi.org/
 **/
abstract class JsonResponseAbstract
{
	/**
	 * Return json object built with content and options
	 **/
	public function getJson()
	{
		$response = [];
		$response['data'] = $this->getData();
		$response['errors'] = $this->getErrors();
		$response['meta'] = $this->getMeta();
		$response = is_null($this->getOptionnalFields()) ?
			$response : array_merge($response, $this->getOptionnalFields());
		return response()->json($response, $this->getHttpCode());
	}


	abstract protected function setHttpCode($httpCode);
	abstract protected function setErrors($errors);
	abstract protected function setData($data);
	abstract protected function setMeta($meta);
	abstract protected function setOptionnalFields($optFields);

	abstract protected function getHttpCode();
	abstract protected function getErrors();
	abstract protected function getData();
	abstract protected function getMeta();
	abstract protected function getOptionnalFields();
}