# service.pp
class idporten_bankid::service inherits idporten_bankid {

  include platform

  if ($platform::deploy_spring_boot) {
    service { $idporten_bankid::service_name:
      ensure => running,
      enable => true,
    }
  }

}
