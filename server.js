<<<<<<< HEAD
import express from "express";
import cors from "cors";
import { sequelize } from "./app/models/index.models.js";


import authRoutes from "./app/routes/auth.routes.js";
import usuarioRoutes from "./app/routes/usuario.routes.js";
import partidoRoutes from "./app/routes/partido.routes.js";
import localidadRoutes from "./app/routes/localidad.routes.js";
import inventarioRoutes from "./app/routes/inventario.routes.js";
import ventaRoutes from "./app/routes/venta.routes.js";

const app = express();

const corsOptions = {
  origin: "http://localhost:8081" 
};

app.use(cors(corsOptions));
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Rutas
app.use("/api/auth", authRoutes);
app.use("/api/usuarios", usuarioRoutes);
app.use("/api/partidos", partidoRoutes);
app.use("/api/localidades", localidadRoutes);
app.use("/api/inventario", inventarioRoutes);
app.use("/api/ventas", ventaRoutes);

// Ruta de bienvenida
app.get("/", (req, res) => {
  res.json({ message: "Bienvenido a la API de Gestión de Boletos." });
});

// Iniciar servidor y BD
sequelize.sync({ alter: true })
  .then(() => {
    console.log("Base de datos sincronizada correctamente");
    
    const PORT = process.env.PORT || 8081;
    app.listen(PORT, () => {
      console.log(`Servidor corriendo en puerto ${PORT}.`);
    });
  })
  .catch(error => {
    console.error("Error sincronizando la base de datos:", error);
  });
=======
  const express = require("express");
  const bodyParser = require("body-parser");
  const cors = require("cors");
  const app = express();

  const corsOptions = {
    origin: "http://localhost:8081"  
    //origin: "*"
  };

  
  app.use(cors(corsOptions));
  app.use(bodyParser.json());
  app.use(bodyParser.urlencoded({ extended: true }));


    const db = require("./API_FINAL/models/index.js");

  db.sequelize.sync()
    .then(() => {
      console.log("Base de datos sincronizada.");
    })
    .catch(err => {
      console.error("Error al sincronizar la base de datos:", err.message);
    });

  require("./API_FINAL/routes/allventa.route.js")(app);
  require("./API_FINAL/routes/inventario.js")(app);
  require("./API_FINAL/routes/localizacion.route.js")(app);
  require("./API_FINAL/routes/partido.route.js")(app);
  require("./API_FINAL/routes/usuario.route.js")(app);
  require("./API_FINAL/routes/venta.route.js")(app);

  app.get("/", (req, res) => {
    res.json({ message: "Bienvenido a la API del Grupo S+." });
  });

  const PORT = process.env.PORT || 8081;
  app.listen(PORT, () => {
    console.log(`Servidor corriendo en el puerto ${PORT}.`);
  });
     
>>>>>>> 3c6d73bcc8204261376583123fbb5910286f4cf0
