<?php

namespace App\Http\tools;

/**
 * JsonResponseFailure class
 * Inherit from JsonResponse class
 * It handle Json failure response when the client action cannot be done
 * 
 * The format of Json follow 2119 RFC with mandatory/optionnal fields
 * @see http://jsonapi.org/format/#errors
 * 
 **/
class JsonResponseFailure extends JsonResponse
{
	/**
	 * Constructor
	 * By default the json response is 400
	 * but it still customisable
	 * @param $httpCode = 400
	 **/
	public function __construct($httpCode = 400)
	{
		parent::__construct($httpCode, []);
	}
}