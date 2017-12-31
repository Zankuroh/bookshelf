<?php

namespace App\Http\Controllers\Api;

use Illuminate\Http\Request;
use App\Models\Friend;
use App\Models\User;
use App\Http\Controllers\ApiController;

/** 
 *   User's friends controller
 * Handle relationship between users
 * From this controller we can handle pretty much all basic feature a friend relationship
 * Complex feature like matchmaking between users's friends are not handled here
 * //TODO maybe for this feature we can do a cron that running the night ?
 * 
 */
class FriendController extends ApiController
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     * // TODO This chunk of code is just an example during travel time
     * 
     * it should be reworked again and again otherwise it will be a waste of time
     * the idea have been to just throw the global workflow and then to see
     * bad and good sides of this feature
     * however next time it should be more concise and well scripted
     * This feature is not only for one purpose but be also extended to others kind of
     * extra features but the main goal is there..
     * 
     */
    public function index()
    {
        $user = User::find($this->getCurrentUser()->id);

        $this->getJsonResponse()->setData($user->getFriends());

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
     * Store a newly created resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request)
    {
        if ($this->_ARV->validate($request,
            [
                "friend_id" => "required|integer"
            ]
        ))
        {
            $friendID = $request->input('friend_id');
            $friend = User::find($friendID);
            if (!is_null($friend))
            {
                $alreadyFriend = !is_null(Friend::where(['user_id' => $this->getCurrentUser()->id,
                                    'friend_id' => $friendID])->first());
                if ($alreadyFriend)
                {
                    $this->setDefaultFailureJsonResponse(false);
                    $this->getJsonResponse()->setOptionnalFields(['title' => 'Already friend.']);
                }
                else
                {
                    Friend::create(['user_id' => $this->getCurrentUser()->id,
                                    'friend_id' => $friendID]);
                    $this->getJsonResponse()->setData($friend);
                }
            }
            else
            {
                $this->setDefaultFailureJsonResponse(false);
                $this->getJsonResponse()->setOptionnalFields(['title' => 'This friend not exist.']);
            }

            //TODO firstly try to parse the identifier, if it's an email so check if it exist in the database
            //otherwise it should be a nickname, in the 
        }
        else
        {
            $this->setDefaultFailureJsonResponse();
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
        //
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
        //
    }

    /**
     * Remove the specified resource from storage.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function destroy(Request $request)
    {
        $success = true;
        if ($this->_ARV->validate($request,
            ['friend_id' => 'required|integer']))
        {
            $friendID = intval($request->input('friend_id'));
            $friendship = Friend::where(['user_id' => $this->getCurrentUser()->id,
                            'friend_id' => $friendID])->first();
            if (is_null($friendship))
            {
                $success = false;
                $this->getJsonResponse()->setOptionnalFields(['title' => 'The friend not exist']);
            }
            else
            {
                $friendship->delete();
            }
            $this->getJsonResponse()->setData(['success' => $success]);
        }
        else
        {
            $this->setDefaultFailureJsonResponse();
        }

        return $this->getRawJsonResponse();
        //
    }
}
