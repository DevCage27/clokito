package com.aplication.clokito

import android.R.attr.onClick
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClokitoApp()
            }
        }
    }
val clokitoFont = FontFamily(
    Font(R.font.poppins_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.poppins_bold, FontWeight.Bold),
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium))

enum class ClokitoSound(var resId: Int) {
    SOM_1(R.raw.chuva),
    SOM_2(R.raw.ondas_binaurais),
    SOM_3(R.raw.ruido_branco),
    SOM_4(R.raw.ruido_marrom),
    SOM_5(R.raw.asmr_no_talking)
}

fun tocarSomPreview (context: Context, rawId: Int):
        MediaPlayer {
    val mediaPlayer = MediaPlayer.create(context, rawId)
    mediaPlayer.setOnCompletionListener {
        it.release()
    }
    mediaPlayer.start()
    return mediaPlayer
}

object SoundManager {
    private var mediaPlayer: MediaPlayer? = null

    fun tocar(rawId: Int, context: Context) {
        mediaPlayer?.stop()
        mediaPlayer?.release()

        mediaPlayer = MediaPlayer.create(context, rawId)
        mediaPlayer?.start()

    }
    fun parar () {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}


@Composable
fun ClokitoApp () {

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "Splash") {
        composable ("Splash")
        { SplashScreen(navController) }
        composable ("Main")
        { MainScreen(navController)  }
        composable ("Timer")
        { TimerScreen(navController) }
        composable ("Sound")
        { SoundScreen(navController) }
    }
}

@Composable
fun SplashScreen (navController: NavHostController) {

    var mostrarSplash by remember { mutableStateOf (true)}
    //timer
    LaunchedEffect(Unit) {
        delay(3000) //o tempo da intro em milissegundos
        mostrarSplash = false
        navController.navigate("Main") {
            popUpTo("Splash") {
                inclusive = true
            }
        }
    }
    if (mostrarSplash) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF7E4)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.clokito_icon_app),
                    contentDescription = "Logo",
                    modifier = Modifier.size(450.dp)
                )
            }
        }
    }

}

@Composable
fun MainScreen(navController: NavController){
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFCEB))
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text (text = "Seja muito bem vindo!",
            fontSize = 28.sp,
            color = (Color(0xFF50392D)),
            fontFamily = clokitoFont,
            fontWeight = FontWeight.Bold
        )
        Text(text = "Escolha seu modo de hoje!",
            fontSize = 20.sp,
            color = (Color(0xFF50392D)),
            fontFamily = clokitoFont,
            fontWeight = FontWeight.Medium
        )
        Image(
            painter = painterResource(id = R.drawable.clokito_saudacao),
            contentDescription = "Logo",
            modifier = Modifier.size(500.dp))

        Spacer(modifier = Modifier.height(50.dp))

        Button(onClick = {
            navController.navigate("Timer")
        },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(
                Color(0xFFFFF9E6F))

            ) {
            Text(
                text = "Clássico",
                fontSize = 25.sp,
                fontFamily = clokitoFont,
                fontWeight = FontWeight.Bold
            )
            }
        Spacer(modifier = Modifier.height(35.dp))
        Button(onClick = {
            navController.navigate("Sound")
        },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(
                Color(0xFFFFF9E6F))

        ) {
            Text(
                text = "Imersivo",
                fontSize = 25.sp,
                fontFamily = clokitoFont,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
fun TimerScreen (navController: NavController) {
    val context = LocalContext.current
    var tempoFoco by remember { mutableStateOf(25 * 60) }
    var tempoPausa by remember { mutableStateOf(5 * 60) }
    var rodando by remember { mutableStateOf(false)}
    var emFoco by remember { mutableStateOf(true)} //Alterna em foco e pausa

    val tempoAtual = if (emFoco) tempoFoco else tempoPausa
    val minutos = tempoAtual / 60
    val segundos = tempoAtual % 60
    val tempoFormatado = String.format("%02d:%02d", minutos, segundos)

    LaunchedEffect(rodando, emFoco) {
        while (rodando && (if (emFoco) tempoFoco > 0 else tempoPausa > 0)) {
            delay(1000L)
            if (emFoco) {
                tempoFoco--
            } else {
                tempoPausa--
            }
        }
        if (rodando && tempoAtual == 0) {
            rodando = false
            emFoco = !emFoco

            Toast.makeText(
                context,"Você completou um ciclo de foco! 🌟 Hora da pausa.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFBF0DC))
                .padding(40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .size(450.dp)
                    .background(Color(0xFFFFF9E6F), shape = RoundedCornerShape(20.dp)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text (
                    text = "Foco",
                    fontSize = 30.sp,
                    fontFamily = clokitoFont,
                    fontWeight = FontWeight.Bold,
                    color = (Color(0xFF50392D))
                )
                Spacer(modifier = Modifier.height(20.dp))
                Box (
                    modifier = Modifier
                        .width(270.dp)
                        .height(90.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFFFCEB)),
                    contentAlignment = Alignment.Center
                ) {
                    Text (
                        text = if (emFoco) tempoFormatado else "00:00",
                        fontSize = 35.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = clokitoFont,
                        color = Color(0xFF50392D)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text (
                    text = "Pausa",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = clokitoFont,
                    color = (Color(0xFF50392D))
                )

                Spacer(modifier = Modifier.height(20.dp))

                Box (
                    modifier = Modifier
                        .width(270.dp)
                        .height(90.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFFFCEB)),
                    contentAlignment = Alignment.Center
                ) {
                    Text (
                        text = if (!emFoco && rodando) tempoFormatado else String.format("%02d:%02d", 5, 0),
                        fontSize = 35.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = clokitoFont,
                        color = Color(0xFF50392D)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { rodando = !rodando },
                    modifier = Modifier
                        .width(240.dp)
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        Color(0xFFFA76A59))
                )
                {
                    Text(
                        text = if (rodando) "Pausar" else "Start",
                        fontSize = 25.sp,
                        fontFamily = clokitoFont,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

        }
    }


@Composable
fun SoundScreen (navController: NavController) {
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var somEscolhido by remember { mutableStateOf<Int?>(null)}

    DisposableEffect(Unit) {
        onDispose { mediaPlayer?.release() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFCEB))
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Escolha o som que vai te acompanhar:",
            fontSize = 28.sp,
            color = (Color(0xFF50392D)),
            fontFamily = clokitoFont,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(50.dp))

        Button(
            onClick = {
                SoundManager.tocar(R.raw.chuva, context)
            },
            modifier = Modifier
                .width(300.dp)
                .height(60.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                Color(0xFFFFF9E6F))
        )
        {
            Text(
                text = "Chuva",
                fontSize = 25.sp,
                fontFamily = clokitoFont,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(35.dp))

        Button(
            onClick = {
                SoundManager.tocar(R.raw.asmr_no_talking, context)
            },
            modifier = Modifier
                .width(300.dp)
                .height(60.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                Color(0xFFFFF9E6F))
        )
        {
            Text(
                text = "Asmr - No Talking",
                fontSize = 25.sp,
                fontFamily = clokitoFont,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(35.dp))

        Button(
            onClick = {
                SoundManager.tocar(R.raw.ondas_binaurais, context)
            },
            modifier = Modifier
                .width(300.dp)
                .height(60.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                Color(0xFFFFF9E6F))
        )
        {
            Text(
                text = "Ondas Binaurais",
                fontSize = 25.sp,
                fontFamily = clokitoFont,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(35.dp))

        Button(
            onClick = {
                SoundManager.tocar(R.raw.ruido_marrom, context)
            },
            modifier = Modifier
                .width(300.dp)
                .height(60.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                Color(0xFFFFF9E6F))
        )
        {
            Text(
                text = "Ruído Marrom",
                fontSize = 25.sp,
                fontFamily = clokitoFont,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(35.dp))

        Button(
            onClick = {
                SoundManager.tocar(R.raw.ruido_branco, context)
            },
            modifier = Modifier
                .width(300.dp)
                .height(60.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                Color(0xFFFFF9E6F))
        )
        {
            Text(
                text = "Ruído Branco",
                fontSize = 25.sp,
                fontFamily = clokitoFont,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(60.dp))

        Button(
            onClick = {
                navController.navigate("Timer")
                somEscolhido?.let { somResId ->
                    SoundManager.tocar(somResId, context)
                }
            },
            modifier = Modifier
                .shadow(
                    elevation = 8.dp,         // altura da sombra
                    shape = RoundedCornerShape(16.dp), // mesmo shape do botão
                    clip = false
                )
                .width(190.dp)
                .height(60.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                Color(0xFFFA76A59))
        )
        {
            Text(
                text = "Iniciar",
                fontSize = 25.sp,
                fontFamily = clokitoFont,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
