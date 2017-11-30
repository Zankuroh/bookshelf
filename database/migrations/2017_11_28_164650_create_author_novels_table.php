<?php

use Illuminate\Support\Facades\Schema;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class CreateAuthorNovelsTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('author_novels', function (Blueprint $table) {
            $table->increments('id');
            $table->unsignedInteger('author_id');
            $table->unsignedInteger('user_id')->comment('added by user id');
            $table->string('isbn', 13);
            $table->unsignedInteger('duration')->comment('duration of the news novels in hour(s)');
            $table->dateTime('expiration')->comment('expiration datetime when the novel should be expired');
            $table->string('title', 140);
            $table->string('content', 600)->nullable();
            $table->timestamps();
            $table->unique(['author_id', 'isbn']);
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::dropIfExists('author_novels');
    }
}
