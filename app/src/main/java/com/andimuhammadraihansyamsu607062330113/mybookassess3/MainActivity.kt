package com.andimuhammadraihansyamsu607062330113.mybookassess3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.andimuhammadraihansyamsu607062330113.mybookassess3.ui.screen.MainScreen
import com.andimuhammadraihansyamsu607062330113.mybookassess3.ui.theme.MyBookAssess2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyBookAssess2Theme {
                MainScreen()
            }
        }
    }
}

