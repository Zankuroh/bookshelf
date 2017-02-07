<?php

use Illuminate\Database\Seeder;

class BooksTableSeeder extends Seeder
{
    /**
     * Run the database seeds.
     *
     * @return void
     */
    public function run()
    {
        $faker = Faker\Factory::create();
        
        for($i = 10 ; $i > 0 ; $i--)
            {
                DB::table('books')->insert([
                    'user_id' => $faker->randomDigit,
                    'isbn' => $faker->isbn13,
                    'category_id' => $faker->randomDigit,
                    'status_id' => $faker->randomDigit
                ]);
            }
        //
    }
}
