import express from "express";
import cors from "cors";
import { sequelize } from "./app/models/index.models.js";

import authRoutes from "./app/routes/auth.routes.js";
import usuarioRoutes from "./app/routes/usuario.routes.js";
import partidoRoutes from "./app/routes/partido.routes.js";
import localidadRoutes from "./app/routes/localidad.routes.js";
import inventarioRoutes from "./app/routes/inventario.routes.js";
import ventaRoutes from "./app/routes/venta.routes.js";
import solicitudRoutes from "./app/routes/solicitud.routes.js";

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
app.use("/api/solicitudes", solicitudRoutes);

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
