package com.vip.pda.file;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vip.pda.R;
import com.vip.pda.adapter.interfaces.OnItemClickListener;
import com.vip.pda.file.AnimUtils;
import com.vip.pda.file.FileAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SharePopup extends PopupWindow {
    private Context context;
    private AnimUtils animUtil;
    private float bgAlpha = 1f;
    private boolean bright = false;
    @BindView(R.id.rv)
    RecyclerView rv;
    FileAdapter adapter;

    public SharePopup(Context context) {
        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.popup_share, null);
        setContentView(contentView);
        ButterKnife.bind(this, contentView);
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.setAdapter(adapter = new FileAdapter(context));
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setAnimationStyle(R.style.PopupAnim);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setOnDismissListener(() -> {
            //消失后,恢复亮度
            toggleBright();
        });
        animUtil = new AnimUtils();
    }

    public void setData(List<String> list) {
        adapter.setNewData(list);
    }

    public void setOnItemClick(OnItemClickListener<String> itemClickListener) {
        adapter.setOnItemClickListener(itemClickListener);
    }

    @OnClick({R.id.btn_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;
        }
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        //消失后,恢复亮度
        toggleBright();
    }

    private void toggleBright() {
        //三个参数分别为： 起始值 结束值 时长 那么整个动画回调过来的值就是从0.5f--1f的
        animUtil.setValueAnimator(0.5f, 1f, 200);
        animUtil.addUpdateListener(new AnimUtils.UpdateListener() {
            @Override
            public void progress(float progress) {
                //此处系统会根据上述三个值，计算每次回调的值是多少，我们根据这个值来改变透明度
                bgAlpha = bright ? progress : (1.5f - progress);//三目运算，应该挺好懂的。
                bgAlpha(bgAlpha);//在此处改变背景，这样就不用通过Handler去刷新了。
            }
        });
        animUtil.addEndListner(new AnimUtils.EndListener() {
            @Override
            public void endUpdate(Animator animator) {
                //在一次动画结束的时候，翻转状态
                bright = !bright;
            }
        });
        animUtil.startAnimator();
    }

    private void bgAlpha(float f) {
        Window window = ((Activity) context).getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.alpha = f;
        window.setAttributes(layoutParams);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }
}
