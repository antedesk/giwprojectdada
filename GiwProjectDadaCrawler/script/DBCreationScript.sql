SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP SCHEMA IF EXISTS `GiwDB` ;
CREATE SCHEMA IF NOT EXISTS `GiwDB` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci ;
USE `GiwDB` ;

-- -----------------------------------------------------
-- Table `GiwDB`.`pagedetails`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `GiwDB`.`pagedetails` ;

CREATE  TABLE IF NOT EXISTS `GiwDB`.`pagedetails` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `productName` VARCHAR(200) NULL ,
  `numberOfReviews` INT NULL ,
  `numberOfReviewsList` INT NULL ,
  `lastDateReviews` DATETIME NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `productName_UNIQUE` (`productName` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `GiwDB`.`page`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `GiwDB`.`page` ;

CREATE  TABLE IF NOT EXISTS `GiwDB`.`page` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `url` VARCHAR(500) NULL ,
  `category` VARCHAR(50) NULL ,
  `iddetails` INT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `url_UNIQUE` (`url` ASC) ,
  INDEX `fk_iddetails` (`iddetails` ASC) ,
  CONSTRAINT `fk_iddetails`
    FOREIGN KEY (`iddetails` )
    REFERENCES `GiwDB`.`pagedetails` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
PACK_KEYS = Default;


-- -----------------------------------------------------
-- Table `GiwDB`.`pagelistaggregation`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `GiwDB`.`pagelistaggregation` ;

CREATE  TABLE IF NOT EXISTS `GiwDB`.`pagelistaggregation` (
  `idPageList` INT NOT NULL ,
  `idPage` INT NOT NULL ,
  PRIMARY KEY (`idPageList`, `idPage`) ,
  INDEX `fk_idPageList` (`idPageList` ASC) ,
  INDEX `fk_idPage` (`idPage` ASC) ,
  CONSTRAINT `fk_idPageList`
    FOREIGN KEY (`idPageList` )
    REFERENCES `GiwDB`.`page` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_idPage`
    FOREIGN KEY (`idPage` )
    REFERENCES `GiwDB`.`page` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
