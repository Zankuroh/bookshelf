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
        if (!$this->_ARV->validate($request,
            [
            'isbn' => 'required|between:9,13'
            ]
            ))
        {
            $this->setDefaultFailureJsonResponse();
        }
        else
        {
            $isbn = $request->input('isbn');
            $reviews = [];
            $rawReviews = Review::select('reviews.id as review_id', 'reviews.isbn', 'reviews.rate', 'reviews.content', 'reviews.created_at', 'users.name', 'reviews.user_id' )->leftJoin('users', 'reviews.user_id', '=', 'users.id')->where('reviews.isbn', '=', $isbn)->get()->all();
            Log::debug($rawReviews);
            foreach ($rawReviews as $rawReview)
            {
                Log::debug($rawReview);
                $tmpReview = [];
                $tmpReview['id'] = $rawReview['review_id'];
                $tmpReview['isbn'] = $rawReview['isbn'];
                $tmpReview['rate'] = $rawReview['rate'];
                $tmpReview['content'] = $rawReview['content'];
                $tmpReview['created_at'] = is_null($rawReview['created_at']) ? 'Unknown' : $rawReview['created_at']->toDateTimeString();
                $tmpReview['user_name'] = is_null($rawReview['name']) ? 'Unknown' : $rawReview['name'];
                $tmpReview['can_edit'] =  $rawReview['user_id'] == $this->getCurrentUser()->id ? 'true' : 'false';
                array_push($reviews, $tmpReview);
            }


            $this->getJsonResponse()->setData(['reviews' => $reviews]);
        }

        return $this->getRawJsonResponse();
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
        $request->merge(['rate' => intval($request->input('rate'))]);
        if (!$this->_ARV->validate($request,
            [
            'isbn' => 'required|between:9,13',
            'content' => 'string|max:500',
            'rate' => 'integer|between:0,5'
            ]
            ))
        {
            $this->setDefaultFailureJsonResponse();
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
                $this->setDefaultFailureJsonResponse();
                $this->getJsonResponse()->setOptionnalFields($failureReasons);
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

                $this->getJsonResponse()->setData(['review' => $newReview]);
            }
        }

        return $this->getRawJsonResponse();
    }

    /**
     * Display the specified resource.
     *
     * @param  int  $id 
     * @return \Illuminate\Http\Response
     */
    public function show($id)
    {
        $review = Review::where('id', $id)->get()->all();
        $this->getJsonResponse()->setData($review);

        return $this->getJsonResponse();
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
        $request->merge(['rate' => intval($request->input('rate'))]);
        $review = Review::find($id);
        if (is_null($review))
        {
            $this->setDefaultFailureJsonResponse(false);
            $this->getJsonResponse()->setOptionnalFields(['title' => 'Review not exist']);

        }
        else
        {
            if (!$this->_ARV->validate($request,
                [
                'content' => 'string|max:500',
                'rate' => 'integer|between:0,5'
                ]))
            {
                $this->setDefaultFailureJsonResponse();
            }
            else
            {
                if ($review->user_id == $this->getCurrentUser()->id)
                {
                    $review->content = $request->input('content');
                    $review->rate = is_null($request->input('rate')) ? DEFAULT_RATE : $request->input('rate');
                    $review->save();
                    $this->getJsonResponse()->setData(['review' => $review]);
                }
                else
                {
                    $failureReasons = ['title' => 'This review is not belongs to user'];
                    $this->setDefaultFailureJsonResponse(false);
                    $this->getJsonResponse()->setOptionnalFields($failureReasons);
                }
            }
        }

        return $this->getRawJsonResponse();
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
        if (!$this->_ARV->validate($request, ['validation' => 'required|accepted']))
        {
            $this->setDefaultFailureJsonResponse();
        }
        else
        {
            $review = Review::find($id);
            if (is_null($review))
            {
                $failureReasons = ['title' => 'Review does not exist.'];
                $this->setDefaultFailureJsonResponse(false);
                $this->getJsonResponse()->setOptionnalFields($failureReasons);
            }
            else
            {
                if ($review->user_id == $this->getCurrentUser()->id)
                {
                    $this->getJsonResponse()->setData(['review' => $review]);
                    $review->delete();
                }
                else
                {
                    $failureReasons = ['title' => 'This review is not belongs to user'];
                    $this->setDefaultFailureJsonResponse(false);
                    $this->getJsonResponse()->setOptionnalFields($failureReasons);
                }
            }
        }

        return $this->getRawJsonResponse();
    }
}
