<?php

use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\Hash;

class UsersTableSeeder extends Seeder
{
    /**
     * Run the database seeds.
     *
     * @return void
     */
    public function run()
    {
        $faker = Faker\Factory::create();

        for ($i = 0; $i < 10 ; $i++)
        {
            DB::table('users')->insert([
            'name' => $faker->name,
            'email' => $faker->email,
            'password' => Hash::make($faker->password),
            'remember_token' => null
            ]);
        }
        

        DB::table('users')->insert([
            'name' => 'testo',
            'email' => 'testo@gmail.com',
            'password' => Hash::make('testo1'),
            'remember_token' => null
            ]);
        //
    }
}
