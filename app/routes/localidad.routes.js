import express from "express";
import { crearLocalidad, listarLocalidades } from "../controllers/localidad.controller.js";
import { verificarToken, esAdmin } from "../middlewares/authJwt.js";

const router = express.Router();

router.post("/", verificarToken, esAdmin, crearLocalidad);
router.get("/", verificarToken, esAdmin, listarLocalidades);

export default router;
