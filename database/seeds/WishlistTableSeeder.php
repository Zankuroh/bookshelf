<?php

use Illuminate\Database\Seeder;

class WishBookSeeder extends Seeder
{
    /**
     * Run the database seeds.
     *
     * @return void
     */
    public function run()
    {
    	$faker = Faker\Factory::create();

    	for($i = 100 ; $i > 0 ; $i--)
    	{
    		DB::table("wishlists")->insert([


    			"user_id" => $i,
    			"isbn" => $faker->isbn13,
    			'hidden' => $faker->boolean
    			]);
    	}
        //
    }
}
