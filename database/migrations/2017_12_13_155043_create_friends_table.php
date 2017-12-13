<?php

use Illuminate\Support\Facades\Schema;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class CreateFriendsTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('friends', function (Blueprint $table) {
            $table->increments('id');
            $table->unsignedInteger("user_id");
            $table->unsignedInteger("friend_id");
            $table->unsignedTinyInteger("status")->default(0); //optionnal
            $table->integer("common_nbr_friends")->default(0); // TODO maybe create a new table to handle this context
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
        Schema::dropIfExists('friends');
    }
}
