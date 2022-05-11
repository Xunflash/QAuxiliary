package com.hicore.hook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import cc.ioctl.util.LayoutHelper;
import com.hicore.ReflectUtil.MField;
import com.hicore.ReflectUtil.MMethod;
import com.hicore.dialog.RepeaterPlusIconSettingDialog;
import com.hicore.messageUtils.QQEnvUtils;
import io.github.qauxv.util.Toasts;
import java.util.HashMap;

@SuppressLint("ResourceType")
public class RepeaterHelper {

    private static final HashMap<String, String> supportMessageTypes = new HashMap<>();

    static {
        supportMessageTypes.put("MessageForPic", "RelativeLayout");
        supportMessageTypes.put("MessageForText", "ETTextView");
        supportMessageTypes.put("MessageForLongTextMsg", "ETTextView");
        supportMessageTypes.put("MessageForFoldMsg", "ETTextView");
        supportMessageTypes.put("MessageForPtt", "BreathAnimationLayout");
        supportMessageTypes.put("MessageForMixedMsg", "MixedMsgLinearLayout");
        supportMessageTypes.put("MessageForReplyText", "SelectableLinearLayout");
        supportMessageTypes.put("MessageForScribble", "RelativeLayout");
        supportMessageTypes.put("MessageForMarketFace", "RelativeLayout");
        supportMessageTypes.put("MessageForTroopPobing", "LinearLayout");
        supportMessageTypes.put("MessageForTroopEffectPic", "RelativeLayout");
        supportMessageTypes.put("MessageForAniSticker", "FrameLayout");
        supportMessageTypes.put("MessageForArkFlashChat", "ArkAppRootLayout");
        supportMessageTypes.put("MessageForShortVideo", "RelativeLayout");
        supportMessageTypes.put("MessageForPokeEmo", "RelativeLayout");
    }

    public static void createRepeatIcon(RelativeLayout baseChatItem, Object ChatMsg, Object session) throws Exception {
        boolean isSendFromLocal;
        int istroop = MField.GetField(ChatMsg, "istroop", int.class);
        if (istroop == 1 || istroop == 0) {
            String UserUin = MField.GetField(ChatMsg, "senderuin", String.class);
            isSendFromLocal = UserUin.equals(QQEnvUtils.getCurrentUin());
        } else {
            isSendFromLocal = MMethod.CallMethodNoParam(ChatMsg, "isSendFromLocal", boolean.class);
        }

        Context context = baseChatItem.getContext();
        String clzName = ChatMsg.getClass().getSimpleName();
        if (supportMessageTypes.containsKey(clzName)) {
            ImageButton imageButton = baseChatItem.findViewById(256667);
            if (imageButton == null) {
                imageButton = new ImageButton(context);
                imageButton.setImageBitmap(RepeaterPlusIconSettingDialog.getRepeaterIcon());
                RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                        LayoutHelper.dip2px(context, 50), LayoutHelper.dip2px(context, 50));
                imageButton.setAdjustViewBounds(true);
                imageButton.getBackground().setAlpha(0);
                imageButton.setMaxHeight(LayoutHelper.dip2px(context, 35));
                imageButton.setMaxWidth(LayoutHelper.dip2px(context, 35));
                imageButton.setId(256667);
                imageButton.setTag(ChatMsg);
                imageButton.setOnClickListener(v -> {
                    try {
                        Repeater.Repeat(session, v.getTag());
                    } catch (Exception e) {
                        Toasts.error(null, e + "");
                    }
                });
                baseChatItem.addView(imageButton, param);
            } else {
                if (imageButton.getVisibility() != View.VISIBLE) {
                    imageButton.setVisibility(View.VISIBLE);
                }
                imageButton.setTag(ChatMsg);
            }

            RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) imageButton.getLayoutParams();
            String attachName = supportMessageTypes.get(clzName);
            View attachView = findView(attachName, baseChatItem);
            if (attachView != null) {
                if (isSendFromLocal) {
                    param.removeRule(RelativeLayout.ALIGN_RIGHT);
                    param.removeRule(RelativeLayout.ALIGN_TOP);
                    param.removeRule(RelativeLayout.ALIGN_LEFT);

                    param.addRule(RelativeLayout.ALIGN_LEFT, attachView.getId());
                    int width = View.MeasureSpec.makeMeasureSpec(0,
                            View.MeasureSpec.UNSPECIFIED);
                    int height = View.MeasureSpec.makeMeasureSpec(0,
                            View.MeasureSpec.UNSPECIFIED);
                    attachView.measure(width, height);

                    int AddedLength = attachView.getTop();
                    AddedLength += attachView.getHeight() / 2 - LayoutHelper.dip2px(context, 50) / 2;

                    int OffsetV = LayoutHelper.dip2px(context, 35);

                    ViewGroup.MarginLayoutParams mLParam = param;
                    mLParam.leftMargin = -OffsetV;
                    mLParam.topMargin = AddedLength;
                } else {
                    param.removeRule(RelativeLayout.ALIGN_RIGHT);
                    param.removeRule(RelativeLayout.ALIGN_TOP);
                    param.removeRule(RelativeLayout.ALIGN_LEFT);
                    param.addRule(RelativeLayout.ALIGN_RIGHT, attachView.getId());
                    int width = View.MeasureSpec.makeMeasureSpec(0,
                            View.MeasureSpec.UNSPECIFIED);
                    int height = View.MeasureSpec.makeMeasureSpec(0,
                            View.MeasureSpec.UNSPECIFIED);
                    attachView.measure(width, height);
                    int AddedLength = attachView.getTop();
                    AddedLength += attachView.getHeight() / 2 - LayoutHelper.dip2px(context, 50) / 2;

                    int OffsetV = LayoutHelper.dip2px(context, 35);
                    ViewGroup.MarginLayoutParams mLParam = param;
                    mLParam.rightMargin = -OffsetV;
                    mLParam.topMargin = AddedLength;
                }
                imageButton.setLayoutParams(param);
            }
        } else {
            ImageButton imageButton = baseChatItem.findViewById(256666);
            if (imageButton != null) {
                baseChatItem.removeView(imageButton);
            }
        }
    }

    public static View findView(String Name, ViewGroup vg) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            if (vg.getChildAt(i).getClass().getSimpleName().contains(Name)) {
                return vg.getChildAt(i);
            }
        }
        return null;
    }
}