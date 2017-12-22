<?php

namespace App\Http\Controllers\Api\Auth;

use App\Http\Controllers\ApiController;
use Illuminate\Http\Request;
use App\Models\User;
use App\Models\PasswordReset;

class ResetPasswordController extends ApiController
{

	public function sendPasswordResetMail($token)
	{
		//find the user with the mail
		//use the method of the User class
		$passwordResetRow = PasswordReset::where(['token' => $token])->first();
		if (!empty($passwordResetRow))
		{
			$email = $passwordResetRow->email;
			$user = User::where('email', '=', $email)->first();
			if (!empty($user))
			{
				if ($user->social_auth != 0)
				{
					$this->getJsonResponse()->setData(['success' => 'false']);
					$this->getJsonResponse()->setOptionnalFields(['title' => 'This email has been logged with a social network.']);
				}
				else
				{
					$this->getJsonResponse()->setData(['title' => 'The email has been sent to ' . $user->email]);
					$this->getJsonResponse()->setData(['success' => 'true']);
					$user->sendPasswordResetMail();
					$passwordResetRow->delete();
					// TODO DELETE THE ROW OF RESET PASSWORD	
				}
			}
			else
			{
				$this->getJsonResponse()->setData(['success' => 'false']);
				$this->getJsonResponse()->setOptionnalFields(['title' => 'Invalid token.']);
			}
		}
		else
		{
			$this->getJsonResponse()->setData(['success' => 'false']);
			$this->getJsonResponse()->setOptionnalFields(['title' => 'Invalid token.']);
		}
		return $this->getRawJsonResponse();
	}

	/**
	 * Send the token to the client to reset the password via mail
	 * Next step is : the client has to send back the token received by mail
	 * to get the new password.
	 * 
	 **/
	public function sendPasswordResetToken(Request $request)
	{
		//find the user with the mail
		//use the method of the User class
		if ($this->_ARV->validate($request, ['email' => 'required|email']))
		{
			$user = User::where('email', '=', $request->input('email'))->first();
			if (is_null($user))
			{
				$this->getJsonResponse()->setData(['success' => 'false']);
				$this->getJsonResponse()->setOptionnalFields(['title' => 'This email is not related to any user']);
			}
			else if ($user->social_auth != 0)
			{
				$this->getJsonResponse()->setData(['success' => 'false']);
				$this->getJsonResponse()->setOptionnalFields(['title' => 'This email has been logged with a social network.']);	
			}
			else
			{
				$this->getJsonResponse()->setData(['title' => 'The email has been sent to ' . $user->email]);
				$this->getJsonResponse()->setData(['success' => 'true']);
				$user->sendPasswordResetTokenMail();
			}
		}
		else
		{
			$this->setDefaultFailureJsonResponse();
		}

		return $this->getRawJsonResponse();
	}

    //
}
