package com.king.drawboard.app

import android.content.ContentValues.TAG
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.PopupWindowCompat
import androidx.lifecycle.lifecycleScope
import com.king.drawboard.app.databinding.ActivityMainBinding
import com.king.drawboard.view.DrawBoardView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    var isGreen = false

    var popup: PopupWindow? = null

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val popContentView by lazy {
        LayoutInflater.from(this).inflate(R.layout.pop_select, null)
    }

    private val popupStrokeView by lazy {
        LayoutInflater.from(this).inflate(R.layout.popup_sketch_stroke, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        popContentView.measure(0, 0)
        popupStrokeView.measure(0, 0)
        binding.ivDrawMode.measure(0, 0)
        binding.test.measure(0, 0)


        binding.drawBoardView.setDrawText("DrawBoard")
        binding.drawBoardView.setDrawBitmap(BitmapFactory.decodeResource(resources, R.drawable.dog))

    }

    private fun showSelectPopupWindow() {
        if (this.popup?.isShowing == true) {
            this.popup?.dismiss()
            return
        }
        Log.d(TAG, "showSelectPopupWindow: 标签未打开")
        val popup = PopupWindow(this)
        popup.isOutsideTouchable = true
        popup.contentView = popContentView

        val padding =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics)
                .toInt()

        val y = popContentView.measuredHeight + binding.ivDrawMode.measuredHeight + padding

        PopupWindowCompat.showAsDropDown(
            popup,
            binding.ivDrawMode,
            -padding,
            -y,
            Gravity.CENTER_HORIZONTAL
        )

        this.popup = popup

    }

    private fun showSelectPenStyle() {
        if (this.popup?.isShowing == true) {
            this.popup?.dismiss()
            return
        }
        Log.d(TAG, "showSelectPenStyle: 标签未打开")
        val popup = PopupWindow(this)
        popup.isOutsideTouchable = true
        popup.contentView = popupStrokeView

        val padding =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics)
                .toInt()

        val y = popupStrokeView.measuredHeight + binding.ivDrawMode.measuredHeight + padding

        PopupWindowCompat.showAsDropDown(
            popup,
            binding.ivDrawMode,
            -padding,
            -y,
            Gravity.CENTER_HORIZONTAL
        )

        this.popup = popup
    }

    private fun changeZoom() {
        binding.drawBoardView.isZoomEnabled = !binding.drawBoardView.isZoomEnabled
    }

    /**
     *  改变绘制模式
     */
    private fun changeDrawMode(@DrawBoardView.DrawMode drawMode: Int, @DrawableRes resId: Int) {
        binding.drawBoardView.drawMode = drawMode
        popup?.dismiss()
        binding.ivDrawMode.setImageResource(resId)
    }

    private fun getExternalFilesDir(context: Context): String? {
        val files: Array<File> = ContextCompat.getExternalFilesDirs(context, "images")
        return if (files.isNullOrEmpty()) context.getExternalFilesDir("images")?.absolutePath else files[0]?.absolutePath
    }

    /**
     * 保存图片
     */
    private fun saveBitmap(bitmap: Bitmap): File? {
        try {
            val str = System.currentTimeMillis().toString()
            val jpg = ".jpg"
            val fileName = str + jpg
            val file = File(getExternalFilesDir(this), fileName)
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.close()
            Log.d("DrawBoard", "file:${file.absolutePath}")
            return file
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun onClick(v: View) {
        Log.d(TAG, "onClick: id " + v.id)
        when (v.id) {
            R.id.ivDrawMode -> showSelectPenStyle()
            R.id.ivPen -> {
                if (!isGreen) {
                    isGreen = true
                    binding.drawBoardView.paintColor = Color.GREEN
                    binding.ivPen.setImageResource(R.drawable.btn_menu_pen_green)
                } else {
                    isGreen = false
                    binding.drawBoardView.paintColor = Color.RED
                    binding.ivPen.setImageResource(R.drawable.btn_menu_pen_red)
                }
            }
            //=======================
            R.id.stroke_color_black -> {
                binding.drawBoardView.paintColor = Color.BLACK
            }
            R.id.stroke_color_red -> {
                binding.drawBoardView.paintColor = Color.RED
            }
            R.id.stroke_color_green -> {
                binding.drawBoardView.paintColor = Color.GREEN
            }
            R.id.stroke_color_cyan -> {
                binding.drawBoardView.paintColor = Color.CYAN
            }
            R.id.stroke_color_blue -> {
                binding.drawBoardView.paintColor = Color.BLUE
            }

            R.id.strokeDraw -> changeDrawMode(
                DrawBoardView.DrawMode.DRAW_PATH,
                R.drawable.stroke_type_rbtn_draw
            )
            R.id.strokeLine -> changeDrawMode(
                DrawBoardView.DrawMode.DRAW_LINE,
                R.drawable.btn_menu_line
            )
            R.id.strokeRect -> changeDrawMode(
                DrawBoardView.DrawMode.DRAW_RECT,
                R.drawable.btn_menu_rect
            )
            R.id.strokeOval -> changeDrawMode(
                DrawBoardView.DrawMode.DRAW_OVAL,
                R.drawable.btn_menu_oval
            )
            R.id.strokePicture->changeDrawMode(
                DrawBoardView.DrawMode.DRAW_BITMAP,
                R.drawable.btn_menu_bitmap
            )
            R.id.strokeText -> changeDrawMode(
                DrawBoardView.DrawMode.DRAW_TEXT,
                R.drawable.btn_menu_text
            )

            R.id.drag -> changeZoom()
            //=======================
            R.id.test -> {
                val str = System.currentTimeMillis()
                Log.d(TAG, "saveBitmap: $str")
            }
            //=======================
            R.id.ivClear -> {
                val dialog = AlertDialog.Builder(this)
                    .setIcon(R.drawable.dog)
                    .setTitle("清空屏幕确认")
                    .setMessage("清空屏幕后无法撤回，请确认是否清空屏幕！")
                    .setPositiveButton("确认", DialogInterface.OnClickListener { _, _ ->
                        Toast.makeText(this@MainActivity, "屏幕已清空", Toast.LENGTH_SHORT).show()
                        binding.drawBoardView.clear()
                    })
                    .setNegativeButton("取消", DialogInterface.OnClickListener { _, _ ->
                        Toast.makeText(this@MainActivity, "已取消", Toast.LENGTH_SHORT).show()
                    })

                dialog.create().show()
            }
            R.id.ivUndo -> binding.drawBoardView.undo()
            R.id.ivRedo -> binding.drawBoardView.redo()
            R.id.ivSave -> {//保存图片
                lifecycleScope.launch {
                    val file = withContext(Dispatchers.IO) {
                        saveBitmap(binding.drawBoardView.imageBitmap)
                    }
                    file?.let {
                        Toast.makeText(this@MainActivity, "保存成功", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            R.id.ivPath -> changeDrawMode(
                DrawBoardView.DrawMode.DRAW_PATH,
                R.drawable.btn_menu_path
            )
            R.id.ivLine -> changeDrawMode(
                DrawBoardView.DrawMode.DRAW_LINE,
                R.drawable.btn_menu_line
            )
            R.id.ivRect -> changeDrawMode(
                DrawBoardView.DrawMode.DRAW_RECT,
                R.drawable.btn_menu_rect
            )
            R.id.ivOval -> changeDrawMode(
                DrawBoardView.DrawMode.DRAW_OVAL,
                R.drawable.btn_menu_oval
            )
            R.id.ivText -> changeDrawMode(
                DrawBoardView.DrawMode.DRAW_TEXT,
                R.drawable.btn_menu_text
            )
            R.id.ivBitmap -> changeDrawMode(
                DrawBoardView.DrawMode.DRAW_BITMAP,
                R.drawable.btn_menu_bitmap
            )
            R.id.ivEraser -> changeDrawMode(
                DrawBoardView.DrawMode.ERASER,
                R.drawable.btn_menu_eraser
            )
        }
    }
}