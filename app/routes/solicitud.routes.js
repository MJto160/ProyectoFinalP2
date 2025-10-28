import express from "express";
import { crearSolicitud, listarSolicitudes, responderSolicitud } from "../controllers/solicitud.controller.js";
import { verificarToken, esAdmin } from "../middlewares/authJwt.js";

const router = express.Router();

// Vendedor crea solicitud
router.post("/", verificarToken, crearSolicitud);

// Admin lista solicitudes
router.get("/", verificarToken, esAdmin, listarSolicitudes);

// Admin responde
router.put("/:id_solicitud", verificarToken, esAdmin, responderSolicitud);

export default router;
