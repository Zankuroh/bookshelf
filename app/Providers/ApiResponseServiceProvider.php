<?php

namespace App\Providers;

use Illuminate\Support\ServiceProvider;
use Illuminate\Contracts\Routing\ResponseFactory;

class ApiResponseServiceProvider extends ServiceProvider
{
    /**
     * Bootstrap the application services.
     *
     * @return void
     */
    public function boot(ResponseFactory $response)
    {
        $response->macro('api', function($data) use ($response)
        {
            $customArr['data'] = 'toto';
            $customArr['errors'] = 'tataerr';
            $customArr['meta'] = 'metasomething';
            return $response->make($customArr);
        });
    }

    /**
     * Register the application services.
     *
     * @return void
     */
    public function register()
    {
        //
    }
}
