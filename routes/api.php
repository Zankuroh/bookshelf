<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Log;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/

/**
 *   Authentication namespace
 */
Route::group(['namespace' => 'Api\Auth'], function() {

    Log::alert('namespace auth group');
    /**
     * Classic way of login
     * 
     **/
    Route::post('auth', 'ApiAuthController@authenticate');

    /**
     * Social network login route
     **/
    Route::post('oauth', 'ApiAuthController@oauthenticate');

    /**
     * Get the token to reset password
     **/
    Route::post('resetpassword', 'ResetPasswordController@sendPasswordResetToken');

    /**
     * Use the token of reset passsword to get a new password by mail
     **/
    Route::get('resetpassword/{token}', function($token)
        {
            $resetPasswordController = new App\Http\Controllers\Api\Auth\ResetPasswordController();
            return $resetPasswordController->sendPasswordResetMail($token);
        }
    );
});

/**
 * Api features namespace
 *
 */
Route::group(['namespace' => 'Api'], function() {



    /**
     *   Registration call, doesn't need JWT authentication
     */
    Route::post('register', 'RegisterController@registerNewUser');

    /**
     *   Calls where JWT authentication is mandatory
     */
    Route::group(['middleware' => 'jwt.auth'], function()
    {

        /**
         * Review group
         **/
        Route::resource('review', 'ReviewController');

        Route::group(['prefix' => 'author'], function()
        {

            Route::get('/', 'AuthorController@index'); // List all authors
            
            /*
            * Store a new authors, need to be confirmed before to be active (by voting system)
            */
            Route::post('/', 'AuthorController@store');
            
            Route::delete('/', 'AuthorController@destroy');

            /**
             * New novels (notifications to users)
             **/
            Route::group(['prefix' => 'novels'], function()
            {
                Route::get('/', 'AuthorNovelsController@index');
                Route::post('/', 'AuthorNovelsController@newNovel');
                Route::delete('/', 'AuthorNovelsController@deleteNovel');

                Route::group(['prefix' => 'notifications'], function()
                {
                    Route::get('/', 'AuthorNovelsController@checkNewNovels');
                });
                
            });

            Route::group(['prefix' => 'subscription'], function()
            {
                Route::post('/', 'AuthorSubscriptionController@subscribe');
                Route::delete('/', 'AuthorSubscriptionController@unSubscribe');
                Route::get('/', 'AuthorSubscriptionController@index');
            });

        });
        
        // Route::get('wishlist', 'WishlistController@index');
        // Route::post('wishlist', 'WishlistController@store');
        // Route::delete('wishlist', 'WishlistController@destroy');

        /** Book group */
        Route::group(['prefix' => 'book'], function()
        {
            /** Get books of user */
            Route::get('/', 'BookController@index');

            /** Store a new book */
            Route::post('/', 'BookController@store');

            /** Delete a book */
            Route::delete('/', 'BookController@destroy');

            /** Update a book */
            Route::put('/', 'BookController@update');
        });

        /**
         *   WIsh group
         *   This wishlist could be generic :
         *   - books,
         *   - BD,
         *   - items,
         *   - etc...
         */
        Route::group(['prefix' => 'wish'], function()
        {

            /** Wish book group */
            Route::group(['prefix' => 'book'], function() {

                /**
                 * Get all wish list of books of user
                 */
                Route::get('/', 'WishBookController@index');

                /** Store a new book into the wishlist */
                Route::post('/', 'WishBookController@store');

                /** Delete a book from wish list */
                Route::delete('/', 'WishBookController@destroy');
            });

        });

        Route::group(["prefix" => "notifications"], function()
        {

        });


        /** Profile group */
        Route::group(['prefix' => 'profile'], function()
        {

            /** Get profile information */
            Route::get('/', 'ProfileController@index');

            /** Delete the user/profile from the app */
            Route::delete('/', 'ProfileController@deleteProfile');

            /** Change profile name */
            Route::post('name', 'ProfileController@changeUserName');

            /** Change email */
            Route::post('email', 'ProfileController@changeEmail');

            /** Change password */
            Route::post('password', 'ProfileController@changePassword');

            /**
             * Look for a profile
             **/
            Route::group(['prefix' => 'search'], function()
            {
                 Route::post('/', 'ProfileController@search');
            });
           
        });

        Route::group(['prefix' => 'friend'], function() 
        {
            // Get friend list
            Route::get('/', 'FriendController@index');

            // Add new friend
            Route::post('/', 'FriendController@store');


            Route::delete('/', 'FriendController@destroy');
        });
    });
});
