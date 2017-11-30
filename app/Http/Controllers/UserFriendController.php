<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\Models\UserFriends;

/** 
 *   User's friends controller
 * Handle relationship between users
 * From this controller we can handle pretty much all basic feature a friend relationship
 * Complex feature like matchmaking between users's friends are not handled here
 * //TODO maybe for this feature we can do a cron that running the night ?
 * 
 */
class UserFriendController extends Controller
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
        $response = getDefaultJsonResponse();
        $acceptedFriendsList = array();
        $pendingFriendsList = array();
        $declinedFriendsList = array();

        $rawList = UserFriends::where(['user_id' => get_current_user()->id]);

        foreach($rawList as $row)
        {
            if ($row->status == UserFriends::STATUS_ACCEPTED)
            {
                $acceptedFriendsList.push($row);

            }
            else if ($row->status == UserFriends::STATUS_PENDING)
            {
                $pendingFriendsList.push($row);
            }
            else if ($row->status == UserFriends::STATUS_DECLINED)
                {
                    $declinedFriendsList.push($row);
                 }
                 // We dont consider others status to be available to send them as response
                 // Specially for the blocked one, the second user should not know this state
                 // otherwise he will be not user-friendly for the app
        }

        $response->json(["acceptedList" => $acceptedList, "pendingList" => $pendingList, "declinedList" => $declinedList]);

        return $response;
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
        $response = getDefaultJsonResponse();

        if ($this->_ARV->validate($request,
            [
                "friend_identifier" => "email|requiredUnless|string"
            ]
        ))
        {
            //TODO firstly try to parse the identifier, if it's an email so check if it exist in the database
            //otherwise it should be a nickname, in the 
        }
        else
        {
            $response = getDefaultFailureJsonResponse();
        }

        return $response;
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
    public function destroy($id)
    {
        //
    }
}
