import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: SkincareHome(),
    );
  }
}

class SkincareHome extends StatelessWidget {
  SkincareHome({super.key});

  final List<Map<String, String>> products = [
    {"name": "CeraVe Cleanser", "price": "250 EGP", "image": "assets/images/cleanser.png"},
    {"name": "La Roche Cleanser", "price": "300 EGP", "image": "assets/images/la_roche.png"},
    {"name": "Vitamin C Serum", "price": "400 EGP", "image": "assets/images/vitamin_c.png"},
    {"name": "Hyaluronic Acid", "price": "350 EGP", "image": "assets/images/hyaluronic.png"},
    {"name": "Moisturizing Cream", "price": "280 EGP", "image": "assets/images/moisturizer.png"},
    {"name": "Night Cream", "price": "320 EGP", "image": "assets/images/night_cream.png"},
    {"name": "Sunscreen SPF 50", "price": "300 EGP", "image": "assets/images/sunscreen.png"},
    {"name": "Aloe Vera Gel", "price": "200 EGP", "image": "assets/images/aloe.png"},
    {"name": "Face Wash", "price": "180 EGP", "image": "assets/images/facewash.png"},
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Skincare Products"),
        centerTitle: true,
        backgroundColor: Colors.pinkAccent,
      ),
      body: ListView.builder(
        itemCount: products.length,
        itemBuilder: (context, index) {
          return Card(
            margin: const EdgeInsets.all(10),
            child: ListTile(
              leading: Image.asset(
                products[index]["image"]!,
                width: 50,
                height: 50,
                fit: BoxFit.cover,
              ),
              title: Text(products[index]["name"]!),
              subtitle: Text(products[index]["price"]!),
            ),
          );
        },
      ),
    );
  }
}
