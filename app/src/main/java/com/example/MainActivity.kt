package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.MissionId
import com.example.data.MissionRunResult
import com.example.ui.AstronautViewModel
import com.example.ui.components.SpaceStarfieldBackground
import com.example.ui.missions.Mission1MoonBaseScreen
import com.example.ui.missions.Mission2RoverRouteScreen
import com.example.ui.missions.Mission3OrbitBasicsScreen
import com.example.ui.missions.Mission4LifeSupportScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MissionSelectionScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ResultsScreen
import com.example.ui.theme.JuniorAstronautTheme

private object AppRoutes {
    const val HOME = "home"
    const val PROFILE = "profile"
    const val MISSIONS = "missions"
    const val PLAY = "play/{missionId}"
    const val RESULTS = "results"

    fun playMission(missionId: MissionId): String = "play/${missionId.name}"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        setContent {
            JuniorAstronautTheme {
                JuniorAstronautApp()
            }
        }
    }
}

@Composable
fun JuniorAstronautApp(
    viewModel: AstronautViewModel = viewModel()
) {
    val navController = rememberNavController()
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val lastResult by viewModel.lastResult.collectAsStateWithLifecycle()

    SpaceStarfieldBackground {
        NavHost(
            navController = navController,
            startDestination = AppRoutes.HOME,
            enterTransition = { fadeIn() + slideInHorizontally { it / 12 } },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() + slideOutHorizontally { it / 12 } }
        ) {
            composable(AppRoutes.HOME) {
                HomeScreen(
                    profileState = profileState,
                    onStartMissionClick = {
                        navController.navigate(AppRoutes.MISSIONS) {
                            launchSingleTop = true
                        }
                    },
                    onProfileClick = {
                        navController.navigate(AppRoutes.PROFILE) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(AppRoutes.PROFILE) {
                ProfileScreen(
                    profileState = profileState,
                    onSaveName = { viewModel.updateAstronautName(it) },
                    onResetProgress = { viewModel.resetTrainingProgress() },
                    onStartMissions = {
                        navController.navigate(AppRoutes.MISSIONS) {
                            launchSingleTop = true
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(AppRoutes.MISSIONS) {
                MissionSelectionScreen(
                    profileState = profileState,
                    onSelectMission = { missionId ->
                        navController.navigate(AppRoutes.playMission(missionId))
                    },
                    onProfileClick = {
                        navController.navigate(AppRoutes.PROFILE) {
                            launchSingleTop = true
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(
                route = AppRoutes.PLAY,
                arguments = listOf(navArgument("missionId") { type = NavType.StringType })
            ) { backStackEntry ->
                val rawId = backStackEntry.arguments?.getString("missionId")
                val missionId = runCatching {
                    MissionId.valueOf(rawId ?: MissionId.MOON_BASE.name)
                }.getOrDefault(MissionId.MOON_BASE)

                val handleComplete: (MissionRunResult) -> Unit = { result ->
                    viewModel.recordMissionCompletion(result)
                    navController.navigate(AppRoutes.RESULTS) {
                        popUpTo(AppRoutes.MISSIONS)
                    }
                }

                when (missionId) {
                    MissionId.MOON_BASE -> Mission1MoonBaseScreen(
                        onMissionComplete = handleComplete,
                        onBackClick = { navController.popBackStack() }
                    )
                    MissionId.ROVER_ROUTE -> Mission2RoverRouteScreen(
                        onMissionComplete = handleComplete,
                        onBackClick = { navController.popBackStack() }
                    )
                    MissionId.ORBIT_BASICS -> Mission3OrbitBasicsScreen(
                        onMissionComplete = handleComplete,
                        onBackClick = { navController.popBackStack() }
                    )
                    MissionId.LIFE_SUPPORT -> Mission4LifeSupportScreen(
                        onMissionComplete = handleComplete,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }

            composable(AppRoutes.RESULTS) {
                val result = lastResult
                if (result != null) {
                    ResultsScreen(
                        result = result,
                        profileState = profileState,
                        onReplayClick = {
                            navController.navigate(AppRoutes.playMission(result.missionId)) {
                                popUpTo(AppRoutes.MISSIONS)
                            }
                        },
                        onNextMissionClick = { nextMission ->
                            navController.navigate(AppRoutes.playMission(nextMission)) {
                                popUpTo(AppRoutes.MISSIONS)
                            }
                        },
                        onMissionSelectClick = {
                            navController.popBackStack(AppRoutes.MISSIONS, inclusive = false)
                        },
                        onHomeClick = {
                            navController.popBackStack(AppRoutes.HOME, inclusive = false)
                        }
                    )
                } else {
                    MissionSelectionScreen(
                        profileState = profileState,
                        onSelectMission = { navController.navigate(AppRoutes.playMission(it)) },
                        onProfileClick = { navController.navigate(AppRoutes.PROFILE) },
                        onBackClick = { navController.popBackStack(AppRoutes.HOME, inclusive = false) }
                    )
                }
            }
        }
    }
}
