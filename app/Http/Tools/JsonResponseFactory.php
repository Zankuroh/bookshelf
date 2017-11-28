<?php

namespace App\Http\tools;

/**
 * class JsonResponseFactory
 * This class is designer to be fit in factory design pattern
 * From the given status, we evalute which class should be returned
 * Actually it handle 'success' and 'failure' status for JsonResponse
 * @return Symfony\Component\HttpFoundation\JsonResponse
 **/
class JsonResponseFactory
{
	/**
	 * Obtain a Json class belongs to the given status
	 * Actually supported status :
	 * 	- 'success'
	 *  	- 'failure'
	 * 
	 * @param $status = 'success' by default
	 * @return JsonResponse object or null if status is incorrect
	 **/
	public static function getJsonResponseByStatus($status = 'success')
	{
		$jsonResponse = null;

		if ($status == 'success')
		{
			$jsonResponse = new JsonResponseSuccess();
		}
		else if ($status == 'failure')
		{
			$jsonResponse = new JsonResponseFailure();
		}

		return $jsonResponse;
	}
}