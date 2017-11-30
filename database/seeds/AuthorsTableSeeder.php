<?php

use Illuminate\Database\Seeder;

class AuthorsTableSeeder extends Seeder
{
    /**
     * Run the database seeds.
     *
     * @return void
     */
    public function run()
    {
        //
        $faker = Faker\Factory::create();

        for($i = 10 ; $i > 0 ; $i--)
        {
           DB::table("authors")->insert([
                'id' => $i,
                'first_name' => $faker->firstName,
                'last_name' => $faker->lastName,
                'active' => $faker->boolean,
                'current_votes' => $faker->randomDigit,
                'created_at' => $faker->dateTimeThisMonth(),
                'updated_at' => $faker->dateTimeThisMonth(),
                'added_by' => $faker->randomDigitNotNull
                ]);
        }
    }
}
