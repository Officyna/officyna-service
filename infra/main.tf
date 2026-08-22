module "db" {
  source = "./db"
}

module "eks" {
  source     = "./eks"
  vpc_id     = module.db.vpc_id
  subnet_ids = module.db.subnet_ids
}