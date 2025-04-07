package es.masanz.pathfinder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

enum class ProviderType{
    BASIC
}
class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setup()
    }
    private fun setup(){
        title="inicio"

    }
}