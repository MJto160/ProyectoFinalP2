import sequelize from "../config/db.config.js";
import UsuarioModel from "./usuario.models.js";
import PartidoModel from "./partido.models.js";
import LocalidadModel from "./localidad.models.js";
import InventarioModel from "./inventario.models.js";
import VentaModel from "./venta.models.js";
import DetalleVentaModel from "./detalleventa.models.js";
import SolicitudModel from "./solicitud.model.js";

// Inicializar modelos
const Usuario = UsuarioModel(sequelize);
const Partido = PartidoModel(sequelize);
const Localidad = LocalidadModel(sequelize);
const Inventario = InventarioModel(sequelize);
const Venta = VentaModel(sequelize);
const DetalleVenta = DetalleVentaModel(sequelize);
const Solicitud = SolicitudModel(sequelize); 


// Relaciones existentes
Usuario.hasMany(Venta, { foreignKey: "id_vendedor" });
Venta.belongsTo(Usuario, { foreignKey: "id_vendedor" });

Partido.hasMany(Inventario, { foreignKey: "id_partido" });
Inventario.belongsTo(Partido, { foreignKey: "id_partido" });

Localidad.hasMany(Inventario, { foreignKey: "id_localidad" });
Inventario.belongsTo(Localidad, { foreignKey: "id_localidad" });

Venta.hasMany(DetalleVenta, { foreignKey: "id_venta" });
DetalleVenta.belongsTo(Venta, { foreignKey: "id_venta" });

Localidad.hasMany(DetalleVenta, { foreignKey: "id_localidad" });
DetalleVenta.belongsTo(Localidad, { foreignKey: "id_localidad" });

Partido.hasMany(DetalleVenta, { foreignKey: "id_partido" });
DetalleVenta.belongsTo(Partido, { foreignKey: "id_partido" });


Usuario.hasMany(Solicitud, { foreignKey: "id_usuario" });
Solicitud.belongsTo(Usuario, { foreignKey: "id_usuario" });

export {
  sequelize,
  Usuario,
  Partido,
  Localidad,
  Inventario,
  Venta,
  DetalleVenta,
  Solicitud,
};
