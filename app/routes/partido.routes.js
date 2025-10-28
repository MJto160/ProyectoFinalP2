import express from "express";
import { crearPartido, listarPartidos } from "../controllers/partido.controller.js";
import { verificarToken, esAdmin } from "../middlewares/authJwt.js";

const router = express.Router();

router.post("/", verificarToken, esAdmin, crearPartido);
router.get("/", verificarToken, listarPartidos);

export default router;
