package es.masanz.pathfinder.Model.Entity

data class OsrmResponse(
    val routes: List<Route>
)

data class Route(
    val geometry: String,
    val legs: List<Leg>
)

data class Leg(
    val steps: List<Step>
)

data class Step(
    val geometry: String,
    val instruction: String
)
