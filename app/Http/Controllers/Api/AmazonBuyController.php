<?php

namespace App\Http\Controllers\Api;

use Illuminate\Http\Request;
use App\Http\Controllers\ApiController;
use Log;

class AmazonBuyController extends ApiController
{
	/**
	 * Generate a research url for amazon with
	 * given keywords fields
	 **/
	public function generateRawAmazonLinkFromSearch($keywordsFields)
	{
		$url_research_amazon = "https://www.amazon.fr/s/?";
		$segmentUrl = "url=" . urlencode("search-alias=aps");
		$segmentFieldsKeywords = 'field-keywords=' . urlencode($keywordsFields);

		$url = $url_research_amazon .
		$segmentUrl .
		'&' . $segmentFieldsKeywords;

		Log::debug("URL decoded = " . $url);
		//$htmlOutput = file_get_contents($url);
		//var_dump($htmlOutput);
		return $url;
	}

	/**
	 * From the research fields we generate the amazon link
	 * to able user to buy the resource
	 **/
	public function generateAmazonLinkFromSearch(Request $request)
	{
		if ($this->_ARV->validate($request,
			['keywords_search' => 'required|string']))
		{
			$url = $this->generateRawAmazonLinkFromSearch($request->input('keywords_search'));
			$this->getJsonResponse()->setData(['url' => $url]);
		}	
		else
		{
			$this->setDefaultFailureJsonResponse();
		}
		
		return $this->getRawJsonResponse();
	}
    //
}
