class idporten_bankid::test_setup inherits idporten_bankid{

  include platform
  include manage_bids
  if ($platform::test_setup) {

    manage_bids::bid_config { $idporten_bankidmobil::application:
      application       => $idporten_bankidmobil::application
    }
  }
}