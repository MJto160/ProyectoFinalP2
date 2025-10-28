// app/routes/reporte.routes.js
import express from "express";
import { ventasPorPartido, ventasPorVendedor } from "../controllers/reporte.controller.js";
import { verifyToken } from "../middlewares/authJwt.js";

const router = express.Router();

// Requieren autenticación 
router.get("/partido/:id_partido", verifyToken, ventasPorPartido);
router.get("/vendedor/:id_vendedor", verifyToken, ventasPorVendedor);

export default router;
