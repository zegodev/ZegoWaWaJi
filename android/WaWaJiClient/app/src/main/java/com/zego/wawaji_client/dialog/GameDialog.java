package com.zego.wawaji_client.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.zego.wawaji.R;
import com.zego.wawaji_client.CMDCenter;
import com.zego.wawaji_client.constants.BoardState;

/**
 * Created by zego on 2018/1/3.
 */

public class GameDialog {

    public Context context;
    public TextView start_text;
    public ImageButton start_game;
    public GameDialog(Context context) {

        this.context = context;
    }

    /**
     * 开始游戏上机弹窗提示
     * @param start
     * @return
     */
    public AlertDialog showIsStartGame(final DialogClick start) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.dialog);
        View view = View
                .inflate(context, R.layout.computer_prompts_dialog, null);

        builder.setView(view);
        builder.setCancelable(true);
        final AlertDialog dialog = builder.create();
        dialog.show();
        ImageButton start_game = view.findViewById(R.id.start_game);
        start_text = view.findViewById(R.id.start_text);
        start_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start.onContinueToPlay(v, dialog);
            }
        });

        final ImageButton giveView = view.findViewById(R.id.give_button);
        giveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start.onGiveUpPlaying(v, dialog);
            }
        });
        dialog.setCancelable(false);

        return dialog;

    }

    /**
     * 游戏结果弹出
     * @param start
     * @param result 游戏结果
     * @return
     */
    public AlertDialog showIsComeAgainGame(final DialogClick start, int result) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.dialog);
        View view = View
                .inflate(context, R.layout.computer_prompts_dialog, null);

        ImageView grab_state = view.findViewById(R.id.grab_state);
        if (result == 0) {
            grab_state.setImageDrawable(context.getResources().getDrawable((R.mipmap.failure)));
        } else {
            grab_state.setImageDrawable(context.getResources().getDrawable((R.mipmap.successful)));

        }
        builder.setView(view);
        builder.setCancelable(true);
        final AlertDialog dialog = builder.create();
        dialog.show();
        start_game = view.findViewById(R.id.start_game);
        start_text = view.findViewById(R.id.start_text);
        start_text.setText(R.string.continue_to_play);
        start_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start.onContinueToPlay(v, dialog);
            }
        });

        final ImageButton giveView = view.findViewById(R.id.give_button);
        TextView givetext = view.findViewById(R.id.givetext);
        givetext.setText("返回娃娃机");
        giveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start.onGiveUpPlaying(v, dialog);
            }
        });

        dialog.setCancelable(false);
        return dialog;

    }

    /**
     * 普通提示
     * @param message
     * @param title
     * @return
     */
    public AlertDialog ShowRemind(String message,String title) {
        AlertDialog dialog = new AlertDialog.Builder(context).setMessage(message).setTitle(title).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();
        dialog.show();

        return dialog;
    }


    public void countdown(String v) {

        if (start_text != null && v != null) {
            start_text.setText(v);
        }

    }

    /**
     * 禁用按钮
     * @param state
     */
    public void setButtonState(boolean state){

        if (start_game != null) {
            start_game.setEnabled(state);
            if(state&&start_game!=null){
               // start_game.setImageDrawable();
            }
        }

    }


    public interface DialogClick {

        public void onContinueToPlay(View v, AlertDialog dialog);

        public void onGiveUpPlaying(View v, AlertDialog dialog);


    }

}
