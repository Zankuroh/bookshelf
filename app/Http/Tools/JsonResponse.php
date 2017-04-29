<?php

namespace App\Http\tools;

class JsonResponse extends JsonResponseAbstract
{
	private $httpCode;
	private $errors;
	private $data;
	private $meta;
	private $optFields;

	public function __construct($httpCode, $errors = null, $data = null, $meta = null, $optFields = [])
	{
		$this->setHttpCode($httpCode);
		$this->setErrors($errors);
		$this->setData($data);
		$this->setMeta($meta);
		$this->setOptionnalFields($optFields);
	}

	public function setHttpCode($httpCode)
	{
		$this->httpCode = $httpCode;
	}

	public function setErrors($errors)
	{
		$this->errors = $errors;
	}

	public function setData($data)
	{
		$this->data = $data;
	}

	public function setMeta($meta)
	{
		$this->meta = $meta;
	}

	public function setOptionnalFields($optFields)
	{
		$this->optFields = $optFields;
	}

	public function getHttpCode()
	{
		return $this->httpCode;
	}

	public function getErrors()
	{
		return $this->errors;
	}

	public function getData()
	{
		return $this->data;
	}

	public function getMeta()
	{
		return $this->meta;
	}

	public function getOptionnalFields()
	{
		return $this->optFields;
	}
}