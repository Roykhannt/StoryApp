package bangkit.roy.storyappmaster.ui

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import bangkit.roy.storyappmaster.R

class MyEditText : AppCompatEditText {

    var typeEditText = ""

    constructor(context: Context) : super(context){
        initializes()    }

    constructor(ctx: Context, attrs: AttributeSet?) : super(ctx, attrs){
        initializes()
    }
    constructor(ctx: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        ctx,
        attrs,
        defStyleAttr
    ){
        initializes()
    }

    private fun initializes() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(c: CharSequence?, start: Int, count: Int, after: Int) {
                // nothing
            }

            override fun onTextChanged(c: CharSequence, start: Int, before: Int, count: Int) {


                if (typeEditText == "password") {
                    if (c.length < 6) {
                        error = context.getString(R.string.paswd_warning)
                    }
                } else if (typeEditText == "email") {
                    if (!Patterns.EMAIL_ADDRESS.matcher(c).matches()) {
                        error = context.getString(R.string.email_warning)
                    }
                } else {
                    if (c.isEmpty()) {
                        error = context.getString(R.string.empty)
                    }
                }
            }

            override fun afterTextChanged(c: Editable?) {
            }
        })
    }

}