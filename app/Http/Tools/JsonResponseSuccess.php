<?php

namespace App\Http\tools;

/**
 * Class JsonResponseSuccess
 * Inherit from JsonResponse
 * This particularity is to set the http code to success
 * Default is 200 as http code
 * 	- data is always an empty array
 *  	- errors is always null
 **/
class JsonResponseSuccess extends JsonResponse
{
	/**
	 * Constructor
	 * Default http code is 200 but it could be customisable
	 * @param $httpCode = 200
	 **/
	public function __construct($httpCode = 200)
	{
		parent::__construct($httpCode, null, []);
	}
}