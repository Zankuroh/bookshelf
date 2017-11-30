<?php

namespace App\Models;

use Laravel\Passport\HasApiTokens;
use Illuminate\Notifications\Notifiable;
use Illuminate\Foundation\Auth\User as Authenticatable;

use Log;
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
        'password', 'remember_token', 'created_at', 'updated_at', 'id'
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
}
