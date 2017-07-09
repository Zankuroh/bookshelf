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
Route::group(['namespace' => 'Auth'], function() {

    Log::alert('namespace auth group');
    Route::post('auth', 'ApiAuthController@authenticate');

    Route::post('oauth', 'ApiAuthController@oauthenticate');
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
    Route::group(['middleware' => 'jwt.auth'], function() {

        /**
         * Review group
         **/
        Route::resource('review', 'ReviewController');

        Route::get('author', 'AuthorController@index'); // List all authors
        Route::post('author', 'AuthorController@store'); // Store a new authors, need to be confirmed before to be active (by voting system)
        Route::delete('author', 'AuthorController@destroy');

        // Route::get('wishlist', 'WishlistController@index');
        // Route::post('wishlist', 'WishlistController@store');
        // Route::delete('wishlist', 'WishlistController@destroy');

        /** Book group */
        Route::group(['prefix' => 'book'], function() {
            /** Get books of user */
            Route::get('/', 'BookController@index');

            /** Store a new book */
            Route::post('/', 'BookController@store');

            /** Delete a book */
            Route::delete('/', 'BookController@destroy');
        });

        /**
         *   WIsh group
         *   This wishlist could be generic :
         *   - books,
         *   - BD,
         *   - items,
         *   - etc...
         */
        Route::group(['prefix' => 'wish'], function() {

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


        
        /** Profile group */
        Route::group(['prefix' => 'profile'], function() {

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
        });
    });
});