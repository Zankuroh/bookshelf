<?php

namespace App\Models;

use Laravel\Passport\HasApiTokens;
use Illuminate\Notifications\Notifiable;
use Illuminate\Foundation\Auth\User as Authenticatable;
use App\Notifications\ResetPasswordNotification;
use App\Notifications\ResetPasswordTokenNotification;
use App\Models\PasswordReset;
use Log;
use Hash;
use Carbon\Carbon;

class User extends Authenticatable
{
    use HasApiTokens, Notifiable;

    /**
     * The attributes that are mass assignable.
     *
     * @var array
     */
    protected $fillable = [
        'name', 'email', 'password',
    ];

    /**
     * The attributes that should be hidden for arrays.
     *
     * @var array
     */
    protected $hidden = [
        'password', 'remember_token', 'created_at', 'updated_at'
    ];

    /**
     *
     * Get wish books of user
     *
     */
    public function wishBooks()
    {
        return $this->hasMany('\App\Models\WishBook');
    }

    /**
     * Get books of users
     *
     */
    public function books()
    {
        return $this->hasMany('\App\Models\Book');
    }

    /**
     * Get suggestions of users
     * 
     **/
    public function suggestions()
    {
        return $this->hasMany('\App\Models\Suggestion');
    }

    /**
     *
     * Set the account as social network logged account
     *
     */
    public function setSocialNetworkFlag()
    {
      $this->social_auth = true;
    }

    /**
    *
    * Search the user by email
    * @return user or null
    */
    public static function getByEmail($email)
    {
      return User::where("email", "=", $email)->first();
    }

    /**
    *
    * Search the user by email if not create a new one
    * The user will not be saved physically
    * @return user or null
    */
    public static function getByEmailOrCreate($email)
    {
        Log::debug("GET BY EMAIL OR CREATE");
      $user = User::getByEmail($email);

      return $user;
    }

    /**
     * Get friends of the user
     * 
     **/
    public function getFriends()
    {
        Log::debug("getFriends from User model for user : " . $this->id);
        return $this->leftJoin('friends', 'users.id', '=', 'friends.friend_id')
        ->where(['friends.user_id' => $this->id])
        ->get();
    }

    /**
     * Send the new password previously reset by the user
     * 
     **/
    public function sendPasswordResetMail()
    {
        $password = str_random(7);
        $this->password = Hash::make($password);
        $this->save();
        Log::debug("sendPasswordResetNotification password=" . $password);
        
        $this->notify(new ResetPasswordNotification($password, $this));

    }

    /**
     * Send the reset password token via email
     * 
     **/
    public function sendPasswordResetTokenMail()
    {
        $token = str_random(7);
        $passwordReset = PasswordReset::create(['email' => $this->email,
                                'token' => $token,
                                'created_at' => Carbon::now()]);
        $passwordReset->save();
        $this->notify(new ResetPasswordTokenNotification($token, $this));
    }
}
