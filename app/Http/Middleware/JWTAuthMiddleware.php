<?php

namespace App\Http\Middleware;

use Closure;

class JWTAuthMiddleware extends \Tymon\JWTAuth\Middleware\GetUserFromToken
{
    /**
     * Handle an incoming request.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  \Closure  $next
     * @return mixed
     */
    public function handle($request, Closure $next)
    {
        $sourceMiddlewareOutput = parent::handle($request, $next);
        return $sourceMiddlewareOutput;



        /**
         * Tricky function,
         * In order to JWTAuthMiddleware does not handle other kind of response other than JSON
         * We need to catch the type of the response in the case where there is an error
         * After that we can format again which our preferred formatted api response service
         */
        if (get_class($sourceMiddlewareOutput) == "Illuminate\Http\JsonResponse")
        {
            \Illuminate\Support\Facades\Log::alert('ne devrait pas passer : ' . get_class($next));
            $jsonFailureResponse = \App\Http\tools\JsonResponseFactory::getJsonResponseByStatus('failure');
            $jsonFailureResponse->setErrors($sourceMiddlewareOutput->getData(true)); 
            $sourceMiddlewareOutput = $jsonFailureResponse->getJson();
        }

        return $sourceMiddlewareOutput;
    }
}
