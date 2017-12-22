<?php

namespace App\Notifications;

use Illuminate\Bus\Queueable;
use Illuminate\Notifications\Notification;
use Illuminate\Contracts\Queue\ShouldQueue;
use Illuminate\Notifications\Messages\MailMessage;
use App\Models\User as User;

class ResetPasswordNotification extends Notification
{
    use Queueable;

    /**
     * New password
     **/
    private $password;
    
    /**
     * User who need to reset password
     **/
    private $user;

    /**
     * Create a new notification instance.
     *
     * @return void
     */
    public function __construct($password, User $user)
    {
        $this->password = $password;
        $this->user = $user;
    }

    /**
     * Get the notification's delivery channels.
     *
     * @param  mixed  $notifiable
     * @return array
     */
    public function via($notifiable)
    {
        return ['mail'];
    }

    /**
     * Get the mail representation of the notification.
     *
     * @param  mixed  $notifiable
     * @return \Illuminate\Notifications\Messages\MailMessage
     */
    public function toMail($notifiable)
    {
        return (new MailMessage)
                    ->level('success')
                    ->subject('Reset your password ' . $this->user->name)
                    ->greeting('Hey, you got a new password !')
                    ->line('Below is your new password:')
                    ->action($this->password, '')
                    ->line('Thank you for using our application!');
    }

    /**
     * Get the array representation of the notification.
     *
     * @param  mixed  $notifiable
     * @return array
     */
    public function toArray($notifiable)
    {
        return [
            //
        ];
    }
}
