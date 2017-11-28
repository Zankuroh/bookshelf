<?php

use Illuminate\Support\Facades\Schema;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

/**
 * User's friends migration class
 * Handle simply the relationship between users
 *
**/
class CreateUserFriendsTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('user_friends', function (Blueprint $table) {
            $table->increments('id');
            $table->unsigned_integer("user_id'");
            $table->unsigned_integer("friend_id");
            $table->unsigned_byte("status");
            $table->integer("common_nbr_friends"); // TODO maybe create a new table to handle this context

            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::dropIfExists('user_friends');
    }
}
