package com.example.foodandart.ui

import com.google.firebase.Firebase
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore

/*
val db = Firebase.firestore
var docData = hashMapOf(
    "coordinates" to arrayListOf(GeoPoint(41.89021, 12.492231), GeoPoint(41.90638859468532, 12.47774498718051)),
    "dates" to arrayListOf("02/05/2024"),
    "description" to "This package includes a complete and guided tour inside the Colosseum, a return shuttle from the meeting point to the Colosseum, and a full lunch with dishes inspired by ancient Rome",
    "images" to arrayListOf("cards/RomaAntica.jpg", "cards/ColosseoInterno.jpg", "cards/MatricianellaEsterno.jpg", "cards/MatricianellaInterno.jpg", "cards/Colosseo.jpg"),
    "price" to 80,
    "title" to "Roma Antica",
    "type" to "Package"
)
db.collection("cardsen").add(docData)
docData = hashMapOf(
"coordinates" to arrayListOf(GeoPoint(41.90638859468532, 12.47774498718051)),
"dates" to arrayListOf("02/05/2024"),
"description" to "The price includes a lunch consisting of a starter, first course, second course, dessert and drinks, which will be inspired by ancient Rome\n",
"images" to arrayListOf("cards/MatricianellaEsterno.jpg", "cards/MatricianellaInterno.jpg"),
"price" to 40,
"title" to "Ristorante Matricianella",
"type" to "Restaurant"
)
db.collection("cardsen").add(docData)
docData = hashMapOf(
"coordinates" to arrayListOf(GeoPoint(41.89021, 12.492231)),
"dates" to arrayListOf("02/05/2024"),
"description" to "Complete and guided tour inside the Colosseum, the visit also includes a return shuttle from the meeting point to the Colosseum\n",
"images" to arrayListOf("cards/ColosseoInterno.jpg", "cards/Colosseo.jpg"),
"price" to 50,
"title" to "Colosseo",
"type" to "Museum"
)
db.collection("cardsen").add(docData)
*/


/*
fun getCardsWithFilters(
    restaurants: Boolean,
    museums: Boolean,
    packages: Boolean,
): MutableList<Query> {
    val db = Firebase.firestore
    val collection = "cards" + Locale.current.language
    val query = mutableListOf<Query>()
    if (restaurants) {
        query.add(db.collection(collection).whereEqualTo("type", "Restaurant"))
    }
    if (museums) {
        query.add(db.collection(collection).whereEqualTo("type", "Museum"))
        Log.d(collection, "museums")
    }
    if (packages) {
        query.add(db.collection(collection).whereEqualTo("type", "Package"))
    }
    if (query.isEmpty()) {
        query.add(db.collection(collection))
    }
    return query
}
 */

/*
LaunchedEffect(key1 = true) {
        val db = Firebase.firestore
        var docData = hashMapOf(
            "date" to "30/06/2024",
            "availability" to 30,
            "booked" to 0,
        )
        getCards().forEach {
            db.collection("cardsit").document(it.key).collection("dates").add(docData)
            db.collection("cardsen").document(it.key).collection("dates").add(docData)
        }
    }
 */