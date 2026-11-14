package com.example.data

enum class MissionId(
    val number: Int,
    val title: String,
    val subtitle: String,
    val topicTag: String,
    val briefDescription: String,
    val keyTakeaway: String
) {
    MOON_BASE(
        number = 1,
        title = "Moon Base",
        subtitle = "Lunar Habitat Planner",
        topicTag = "Survival & Engineering",
        briefDescription = "Choose the right shelter, oxygen, power, water, and food systems to keep astronauts safe on the Moon.",
        keyTakeaway = "A safe Moon base uses shielded pressurized domes, solar + battery power, recycled water, and lunar ice extraction."
    ),
    ROVER_ROUTE(
        number = 2,
        title = "Rover Route",
        subtitle = "Crater Obstacle Course",
        topicTag = "Navigation & Robotics",
        briefDescription = "Steer your planetary rover across rocky craters, collect bonus science samples, and reach the target beacon.",
        keyTakeaway = "Space rovers plan careful routes around deep craters and boulders because help from Earth is hundreds of thousands of kilometers away."
    ),
    ORBIT_BASICS(
        number = 3,
        title = "Orbit Basics",
        subtitle = "Satellite Trajectory Sim",
        topicTag = "Gravity & Motion",
        briefDescription = "Adjust launch speed and direction in a live Earth simulation to place a satellite into a stable orbit.",
        keyTakeaway = "An orbit happens when a spacecraft moves forward fast enough (~7.8 km/s in Low Earth Orbit) that gravity curves its path continuously around Earth."
    ),
    LIFE_SUPPORT(
        number = 4,
        title = "Life Support",
        subtitle = "Resource Balance Challenge",
        topicTag = "Closed-Loop Systems",
        briefDescription = "Balance oxygen, water, and electrical power across changing space conditions without letting any system run dry.",
        keyTakeaway = "Spacecraft life support links power, water, and air together—electricity recycles water and splits H₂O into breathable oxygen."
    );

    fun nextMissionOrNull(): MissionId? = when (this) {
        MOON_BASE -> ROVER_ROUTE
        ROVER_ROUTE -> ORBIT_BASICS
        ORBIT_BASICS -> LIFE_SUPPORT
        LIFE_SUPPORT -> null
    }
}

data class AstronautRank(
    val title: String,
    val minStars: Int,
    val nextRankStars: Int?,
    val badgeTitle: String
) {
    companion object {
        fun fromStars(totalStars: Int): AstronautRank = when {
            totalStars >= 12 -> AstronautRank(
                title = "Junior Commander",
                minStars = 12,
                nextRankStars = null,
                badgeTitle = "Master Space Explorer"
            )
            totalStars >= 9 -> AstronautRank(
                title = "Mission Specialist",
                minStars = 9,
                nextRankStars = 12,
                badgeTitle = "Science & Systems Ace"
            )
            totalStars >= 6 -> AstronautRank(
                title = "Orbital Pilot",
                minStars = 6,
                nextRankStars = 9,
                badgeTitle = "Trajectory Navigator"
            )
            totalStars >= 3 -> AstronautRank(
                title = "Lunar Explorer",
                minStars = 3,
                nextRankStars = 6,
                badgeTitle = "Surface Pathfinder"
            )
            else -> AstronautRank(
                title = "Space Cadet",
                minStars = 0,
                nextRankStars = 3,
                badgeTitle = "Beginner Astronaut Badge"
            )
        }
    }
}

data class AstronautProfileState(
    val astronautName: String = "Cadet Nova",
    val missionStars: Map<MissionId, Int> = MissionId.entries.associateWith { 0 },
    val missionBestScores: Map<MissionId, Int> = MissionId.entries.associateWith { 0 },
    val isLoaded: Boolean = false
) {
    val totalStars: Int
        get() = missionStars.values.sum()

    val maxPossibleStars: Int
        get() = MissionId.entries.size * 3

    val completedMissionsCount: Int
        get() = missionStars.values.count { it > 0 }

    val totalBestScore: Int
        get() = missionBestScores.values.sum()

    val rank: AstronautRank
        get() = AstronautRank.fromStars(totalStars)
}

data class MissionRunResult(
    val missionId: MissionId,
    val score: Int,
    val maxScore: Int,
    val starsEarned: Int,
    val summaryDetail: String
)

data class EducationalSource(
    val title: String,
    val description: String,
    val url: String
)

val NasaEducationalSources = listOf(
    EducationalSource(
        title = "NASA Space Place",
        description = "Kid-friendly articles, games, and Earth & space science facts.",
        url = "https://spaceplace.nasa.gov/"
    ),
    EducationalSource(
        title = "NASA STEM Engagement",
        description = "Hands-on activities and learning guides for students.",
        url = "https://www.nasa.gov/learning-resources/"
    ),
    EducationalSource(
        title = "NASA Science: Solar System",
        description = "Explore planets, moons, rovers, and orbital missions.",
        url = "https://science.nasa.gov/solar-system/"
    )
)
