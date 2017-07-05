<?php

namespace App\Http\Controllers\Api;

use Illuminate\Http\Request;
use App\Models\Review as Review;
use Validator;
use Log;

class ReviewController extends \App\Http\Controllers\ApiController
{
    /**
     * Default rate whether the user has not defined
     */
    const DEFAULT_RATE = 5;


    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index(Request $request)
    {
        $response = $this->getDefaultJsonResponse();
        if (!$this->_ARV->validate($request,
            [
            'isbn' => 'required|between:9,13'
            ]
            ))
        {
            $response = $this->getDefaultFailureJsonResponse();
        }
        else
        {
            $isbn = $request->input('isbn');
            $reviews = [];
            $rawReviews = Review::leftJoin('users', 'users.id', '=', 'reviews.user_id')->where('reviews.isbn', '=', $isbn)->get()->all();
            foreach ($rawReviews as $rawReview)
            {
                $tmpReview = [];
                $tmpReview['id'] = $rawReview['id'];
                $tmpReview['isbn'] = $rawReview['isbn'];
                $tmpReview['rate'] = $rawReview['rate'];
                $tmpReview['content'] = $rawReview['content'];
                $tmpReview['created_at'] = is_null($rawReview['created_at']) ? 'Unknown' : $rawReview['created_at']->toDateTimeString();
                $tmpReview['user_name'] = is_null($rawReview['name']) ? 'Unknown' : $rawReview['name'];
                $tmpReview['can_edit'] =  $rawReview['user_id'] == $this->getCurrentUser()->id ? 'true' : 'false';
                array_push($reviews, $tmpReview);
            }


            $response->setData(['reviews' => $reviews]);
        }

        return $response->getJson();
    }

    /**
     * Show the form for creating a new resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function create()
    {
        //
    }

    /**
     * Store a new review in database
     * Required fields :
     *     - isbn
     *     - content 
     *     - rate
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request)
    {
        $response = $this->getDefaultJsonResponse();
        $request->merge(['rate' => intval($request->input('rate'))]);

        if (!$this->_ARV->validate($request,
            [
            'isbn' => 'required|between:9,13',
            'content' => 'string|max:500',
            'rate' => 'integer|between:0,5'
            ]
            ))
        {
            $response = $this->_ARV->getFailureJson();
        }
        else
        {
            $isbn = $request->input('isbn');
            $user_id = $this->getCurrentUser()->id;

            if (Review::where(
                [
                'isbn' => $isbn,
                'user_id' => $user_id
                ])->get()->isNotEmpty())
            {
                $failureReasons = ['title' => 'Review on this book already exist'];
                $response = $this->_ARV->getFailureJson();
                $response->setOptionnalFields($failureReasons);
            }
            else
            {
                $newReview = Review::create(
                    [
                    'isbn' => $isbn,
                    'user_id' => $user_id,
                    'content' => $request->input('content'),
                    'rate' => is_null($request->input('rate')) ? DEFAULT_RATE : $request->input('rate')
                    ]);

                $response->setData(['review' => $newReview]);
            }
        }

        return $response->getJson();
        //
    }

    /**
     * Display the specified resource.
     *
     * @param  int  $id 
     * @return \Illuminate\Http\Response
     */
    public function show($id)
    {
        $response = $this->getDefaultJsonResponse();
        $review = Review::where('id', $id)->get()->all();
        $response->setData($review);

        return $response->getJson();
    }

    /**
     * Show the form for editing the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function edit($id)
    {
        //
    }

    /**
     * Update the specified resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function update(Request $request, $id)
    {
        $response = $this->getDefaultJsonResponse();
        $request->merge(['rate' => intval($request->input('rate'))]);
        $review = Review::find($id);

        if (is_null($review))
        {
            $response = $this->getDefaultFailureJsonResponse(false);
            $response->setOptionnalFields(['title' => 'Review not exist']);

        }
        else
        {
            if (!$this->_ARV->validate($request,
                [
                'content' => 'string|max:500',
                'rate' => 'integer|between:0,5'
                ]))
            {
                $response = $this->getDefaultFailureJsonResponse();
            }
            else
            {
                if ($review->user_id == $this->getCurrentUser()->id)
                {
                    $review->content = $request->input('content');
                    $review->rate = is_null($request->input('rate')) ? DEFAULT_RATE : $request->input('rate');
                    $review->save();
                    $response->setData(['review' => $review]);
                }
                else
                {
                    $response = $this->getDefaultFailureJsonResponse(false);
                    $failureReasons = ['title' => 'This review is not belongs to user'];
                    $response->setOptionnalFields($failureReasons);
                }
            }
        }

        return $response->getJson();
        //
    }

    /**
     * Remove the specified resource from storage.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function destroy(Request $request, $id)
    {
        $response = $this->getDefaultJsonResponse();

        if (!$this->_ARV->validate($request, ['validation' => 'required|accepted']))
        {
            $response = $this->getDefaultFailureJsonResponse();
        }
        else
        {
            $review = Review::find($id);
            if (is_null($review))
            {
                $response = $this->getDefaultFailureJsonResponse(false);
                $failureReasons = ['title' => 'Review does not exist.'];
                $response->setOptionnalFields($failureReasons);
            }
            else
            {
                if ($review->user_id == $this->getCurrentUser()->id)
                {
                    $response->setData(['review' => $review]);
                    $review->delete();
                }
                else
                {
                    $response = $this->getDefaultFailureJsonResponse(false);
                    $failureReasons = ['title' => 'This review is not belongs to user'];
                    $response->setOptionnalFields($failureReasons);
                }
            }
        }

        return $response->getJson();
    }
}
