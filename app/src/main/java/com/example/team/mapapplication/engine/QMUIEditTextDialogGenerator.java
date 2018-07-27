package com.example.team.mapapplication.engine;

import android.content.Context;
import android.text.InputType;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogBuilder;

/**
 * Created by Ellly on 2018/7/26.
 */

public abstract class QMUIEditTextDialogGenerator {

    private final QMUIDialog mDialog;
    private QMUIDialog.EditTextDialogBuilder mBuilder;

    public QMUIEditTextDialogGenerator(Context context, String title){
        mBuilder = new QMUIDialog.EditTextDialogBuilder(context)
                .setTitle(title)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                dialog.dismiss();
            }
        })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        onPositiveClick(dialog, index, getText());
                    }
                });
        mDialog = mBuilder.create();
        mBuilder.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }

    protected abstract void onPositiveClick(QMUIDialog dialog, int index, String text);

    public QMUIDialog getDialog(){
        return mDialog;
    }

    public String getText(){
        if (mBuilder != null){
            return mBuilder.getEditText().getText().toString();
        }else {
            return "";
        }
    }
}
