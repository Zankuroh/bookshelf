<?php

use Illuminate\Http\Request;

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

Route::get('/user', function (Request $request) {
    return $request->user();
})->middleware('auth:api');


Route::post('/auth', 'Auth\ApiAuthController@authenticate');
Route::post('/register', 'ApiRegisterController@registerNewUser');
Route::get('/books', 'ApiBooksController@index'); // LIST ALL BOOKS
Route::post('/books', 'ApiBooksController@store'); // STORE NEW BOOK
Route::delete('/books', 'ApiBooksController@destroy')->middleware('jwt.auth'); // DELETE BOOK
