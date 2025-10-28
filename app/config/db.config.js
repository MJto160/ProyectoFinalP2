import { Sequelize } from "sequelize";

const sequelize = new Sequelize(
  'neondb',
  'neondb_owner',
  'npg_MUHKi8bxfsr0',
  {
    host: 'ep-falling-star-af2013gj-pooler.c-2.us-west-2.aws.neon.tech',
    dialect: 'postgres',
    dialectOptions: {
      ssl: {
        require: true,
        rejectUnauthorized: false
      }
    }
  }
);

export default sequelize;
